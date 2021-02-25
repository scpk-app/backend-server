package dev.scpk.scpk.controllers;

import dev.scpk.scpk.converters.PaymentGroupPropertyEditor;
import dev.scpk.scpk.converters.UserPropertyEditor;
import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class MainController {
    @Autowired
    private PaymentGroupPropertyEditor paymentGroupPropertyEditor;

    @Autowired
    private UserPropertyEditor userPropertyEditor;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.registerCustomEditor(UserDAO.class, userPropertyEditor);
        webDataBinder.registerCustomEditor(PaymentGroupDAO.class, paymentGroupPropertyEditor);
    }
}
