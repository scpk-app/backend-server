package dev.scpk.scpk.converters;

import dev.scpk.scpk.dao.PaymentGroupDAO;
import dev.scpk.scpk.services.PaymentGroupService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

@Component
public class PaymentGroupPropertyEditor extends PropertyEditorSupport {
    @Autowired
    private PaymentGroupService paymentGroupService;

    @Override
    public String getAsText() {
        PaymentGroupDAO paymentGroupDAO = (PaymentGroupDAO) this.getValue();
        return paymentGroupDAO.getId().toString();
    }

    @SneakyThrows
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        PaymentGroupDAO paymentGroupDAO =
                this.paymentGroupService.findById(
                        Long.valueOf(text)
                );
        this.setValue(paymentGroupDAO);
    }
}
