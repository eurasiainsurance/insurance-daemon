package tech.lapsa.insurance.daemon;

import java.time.Instant;
import java.util.Properties;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.domain.PaymentMethod;
import tech.lapsa.epayment.domain.QazkomPayment;
import tech.lapsa.epayment.facade.beans.EpaymentFacadeBean;
import tech.lapsa.insurance.facade.InsuranceRequestFacade;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.javax.jms.ObjectConsumerListener;

@MessageDriven(mappedName = PaymentCompleteDrivenBean.JNDI_JMS_DEST)
public class PaymentCompleteDrivenBean extends ObjectConsumerListener<Invoice> {

    public PaymentCompleteDrivenBean() {
	super(Invoice.class);
    }

    public static final String JNDI_JMS_DEST = EpaymentFacadeBean.JNDI_JMS_DEST_PAID_EBILLs;

    @Inject
    private InsuranceRequestFacade insuranceRequests;

    @Override
    public void accept(Invoice invoice, Properties properties) {
	switch (invoice.getPayment().getMethod()) {
	case QAZKOM:
	    String methodName = PaymentMethod.QAZKOM.regular();
	    QazkomPayment qp = MyObjects.requireA(invoice, QazkomPayment.class);
	    Integer id = Integer.valueOf(invoice.getExternalId());
	    Instant paid = qp.getCreated();
	    String ref = qp.getReference();
	    Double amount = qp.getAmount();
	    insuranceRequests.markPaymentSuccessful(id, methodName, paid, amount, ref);
	    break;
	}
    }
}
