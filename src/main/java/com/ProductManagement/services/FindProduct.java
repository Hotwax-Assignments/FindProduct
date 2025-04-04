package com.ProductManagement.services;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.service.ModelService;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.math.BigDecimal;

public class FindProduct {

    private static final String MODULE = FindProduct.class.getName();

    public static Map<String, Object> findProduct(DispatchContext dctx, Map<String, Object> context) {
        String productId = (String) context.get("productId");
        String productName = (String) context.get("productName");
        String featureType = (String) context.get("featureType");
        String priceFrom = (String) context.get("priceFrom");
        String priceTo = (String) context.get("priceTo");

        Debug.logInfo("Searching for product with id: " + productId + ", productName: " + productName + ", featureType: " + featureType + ", priceFrom: " + priceFrom + ", priceTo: " + priceTo, MODULE);

        Map<String, Object> result = new HashMap<>();

        try {
            Delegator delegator = dctx.getDelegator();

            List<EntityCondition> conditions = new ArrayList<>();

            if (productId != null && !productId.isEmpty()) {
                Debug.logInfo("Product ID is not null", MODULE);
                conditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
            }
            if (productName != null && !productName.isEmpty()) {
                conditions.add(EntityCondition.makeCondition("productName", EntityOperator.EQUALS, productName));
            }
            if (featureType != null && !featureType.isEmpty()) {
                conditions.add(EntityCondition.makeCondition("productFeatureTypeId", EntityOperator.EQUALS, featureType));
            }

            if (priceFrom != null && !priceFrom.isEmpty() && priceTo != null && !priceTo.isEmpty()) {

                BigDecimal fromPrice = new BigDecimal(priceFrom);
                BigDecimal toPrice = new BigDecimal(priceTo);

                conditions.add(EntityCondition.makeCondition("price", EntityOperator.GREATER_THAN_EQUAL_TO, fromPrice));
                conditions.add(EntityCondition.makeCondition("price", EntityOperator.LESS_THAN_EQUAL_TO, toPrice));
            } else if (priceFrom != null && !priceFrom.isEmpty()) {
                BigDecimal fromPrice = new BigDecimal(priceFrom);
                conditions.add(EntityCondition.makeCondition("price", EntityOperator.GREATER_THAN_EQUAL_TO, fromPrice));
            } else if (priceTo != null && !priceTo.isEmpty()) {
                BigDecimal toPrice = new BigDecimal(priceTo);
                conditions.add(EntityCondition.makeCondition("price", EntityOperator.LESS_THAN_EQUAL_TO, toPrice));
            }

            List<GenericValue> products = EntityQuery.use(delegator).from("FindProductView").where(conditions)
                                                     .distinct()
                                                     .queryList();
            
            if (products != null && !products.isEmpty()) {
                Debug.logInfo("Found products: " + products.size(), MODULE);
                result.put("productList", products);
            } else {
                result.put("productList", new ArrayList<>());
            }

        }catch (GenericEntityException e) {
            Debug.logError(e, "Error occurred while searching for products", MODULE);
            result.put(ModelService.ERROR_MESSAGE, "Error occurred while searching for products: " + e.getMessage());
            return ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }
}