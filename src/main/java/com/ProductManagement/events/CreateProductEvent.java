package com.ProductManagement.events;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;

public class CreateProductEvent {

    public static final String MODULE = CreateProductEvent.class.getName();

    public static String createProductEvent(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");


        String productName = request.getParameter("productName");
        String productCategoryId = request.getParameter("productCategoryId");
        String price = request.getParameter("price");
        
        if (UtilValidate.isEmpty(productName) || UtilValidate.isEmpty(productCategoryId)|| UtilValidate.isEmpty(price)) {
            String errMsg = "Product Name ,Category and Price are required fields on the form and can't be empty.";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        try {
            Debug.logInfo("=======Creating Product record in event using service createProductService=========", MODULE);
            dispatcher.runSync("CreateProductService", UtilMisc.toMap("productName", productName,
                    "productCategoryId", productCategoryId, "price", price,"userLogin", userLogin));
        } catch (GenericServiceException e) {
            String errMsg = "Unable to create new records in Product entity: " + e.toString();
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        request.setAttribute("_EVENT_MESSAGE_", "Product created succesfully.");
        return "success";
    }
}