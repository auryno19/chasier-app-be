package com.example.chasier_app_be.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.chasier_app_be.model.DailyOrderCounter;
import com.example.chasier_app_be.model.Order;
import com.example.chasier_app_be.model.OrderItem;
import com.example.chasier_app_be.model.Product;
import com.example.chasier_app_be.model.PromoApplied;
import com.example.chasier_app_be.model.Promotion;
import com.example.chasier_app_be.model.Promotion.PromoTarget;
import com.example.chasier_app_be.model.Promotion.PromoType;
import com.example.chasier_app_be.repository.OrderRepository;
import com.example.chasier_app_be.repository.ProductRepository;
import com.example.chasier_app_be.repository.PromotionRepository;
import com.example.chasier_app_be.util.NotFoundException;
import com.example.chasier_app_be.util.ValidationException;

@Service
public class OrderService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PromotionService promotionService;

    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MongoTransactionManager transactionManager;

    public double calculateTotalAmount(Order order) {
        double totalAmount = 0;
        for (OrderItem item : order.getItems()) {
            totalAmount += item.getTotalPrice();
        }
        return totalAmount;
    }

    public double calculateTotalDiscount(Order order) {
        double totalDiscount = 0;
        for (OrderItem item : order.getItems()) {
            if (item.getPromoApplied() != null) {
                totalDiscount += item.getPromoApplied().getDiscountAmount();
            }
        }
        if (order.getPromoApplied() != null) {
            totalDiscount += order.getPromoApplied().getDiscountAmount();
        }
        return totalDiscount;
    }

    public String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));

        Query query = new Query(Criteria.where("_id").is(date));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);

        DailyOrderCounter counter = mongoTemplate.findAndModify(query, update, options, DailyOrderCounter.class);
        String orderSeq = String.format("%04d", counter.getSeq());

        return "ORDER-" + date + "-" + orderSeq;
    }

    public PromoApplied findPromo(OrderItem item, String categoryId, double amount) {

        this.promotionService.checkedPromo();
        Criteria criteria = new Criteria();
        criteria.and("isActive").is(true);

        if (item == null && categoryId == null) {
            criteria.and("target").is(PromoTarget.ORDER);
        }
        if (categoryId != null) {
            criteria.and("target").is(PromoTarget.CATEGORY);
            criteria.and("condition.categoryIds").in(categoryId);
        }
        if (item != null && categoryId == null) {
            criteria.and("target").is(PromoTarget.PRODUCT);
            criteria.and("condition.productIds").in(item.getProductId());
            Product product = productRepository.findId(item.getProductId()).orElse(null);
            if (product == null) {
                throw new NotFoundException(categoryId);
            }
        }
        Query query = new Query(criteria);
        Promotion promo = mongoTemplate.findOne(query, Promotion.class);
        double discount = 0.0;
        if (promo == null) {
            return null;
        }
        if (promo.getCondition().getCategoryIds() != null) {
            if (amount <= promo.getCondition().getMinTotalAmount()) {
                return null;
            }
        }
        switch (promo.getType()) {
            case PromoType.PERCENTAGE:
                if (item != null || categoryId != null) {
                    discount = promo.getValue() / 100 * item.getPrice();
                } else {
                    discount = promo.getValue() / 100 * amount;
                }
                break;
            case PromoType.FIXED:
                discount = promo.getValue();
                break;
            case PromoType.BUY_X_GET_Y:
                if (item != null && item.getQuantity() >= promo.getCondition().getBuyQty()) {
                    discount = item.getPrice() * promo.getCondition().getGetQty();
                }
                break;
            default:
                break;
        }

        PromoApplied promoApplied = new PromoApplied();
        promoApplied.setPromoId(promo.getId());
        promoApplied.setPromoName(promo.getName());
        promoApplied.setDiscountAmount(discount);
        return promoApplied;
    }

    public void applyBestPromo(OrderItem item, double totalAmount) {
        Product product = productRepository.findId(item.getProductId()).orElse(null);
        if (product == null) {
            return;
        }
        String categoryId = product.getCategoryId();
        PromoApplied promoProduct = findPromo(item, null, totalAmount);
        PromoApplied promoCategory = findPromo(item, categoryId, totalAmount);

        PromoApplied bestPromo = null;
        if (promoProduct != null && promoCategory != null) {
            bestPromo = promoProduct.getDiscountAmount() >= promoCategory.getDiscountAmount() ? promoProduct
                    : promoCategory;
        } else if (promoProduct != null) {
            bestPromo = promoProduct;
        } else if (promoCategory != null) {
            bestPromo = promoCategory;
        }

        if (bestPromo != null) {
            item.setPromoApplied(bestPromo);

            Promotion promo = promotionRepository.findId(bestPromo.getPromoId()).orElse(null);
            if (promo != null && promo.getType() == PromoType.BUY_X_GET_Y) {
                item.setQuantity(item.getQuantity() + promo.getCondition().getGetQty());
            }
        }

    }

    public void updateStock(OrderItem item) {
        Product product = productRepository.findId(item.getProductId()).orElse(null);
        if (product != null) {
            if (product.getStock() < item.getQuantity()) {
                throw new ValidationException("Stock not enough", null);
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
    }

    @Transactional
    public String manageOrder(Order order) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        return transactionTemplate.execute(status -> {

            String orderNumber = generateOrderNumber();
            order.setOrderNumber(orderNumber);

            double totalAmount = calculateTotalAmount(order);
            order.setTotalAmount(totalAmount);
            for (OrderItem item : order.getItems()) {
                applyBestPromo(item, totalAmount);
                updateStock(item);
            }
            PromoApplied promo = findPromo(null, null, totalAmount);
            if (promo != null) {
                order.setPromoApplied(promo);
            } else {
                order.setPromoApplied(null);
            }

            order.setStatus("PENDING");
            orderRepository.save(order);

            return order.getId();

        });
    }
}
