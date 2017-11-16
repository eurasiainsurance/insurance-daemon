package tech.lapsa.insurance.daemon;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.time.Instant;
import java.util.Properties;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import tech.lapsa.epayment.domain.APayment;
import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.facade.beans.EpaymentFacadeBean;
import tech.lapsa.insurance.facade.InsuranceRequestFacade;
import tech.lapsa.java.commons.function.MyExceptions;
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
	MyObjects.requireNonNull(invoice, "invoice");
	invoice.optionalPayment()
		.orElseThrow(MyExceptions.illegalStateSupplierFormat("No payment attached %1$s", invoice));
	APayment qp = invoice.getPayment();
	String methodName = invoice.getPayment().getMethod().regular();
	Integer id = Integer.valueOf(invoice.getExternalId());
	Instant paid = qp.getCreated();
	String ref = qp.getReference();
	Double amount = qp.getAmount();
	reThrowAsUnchecked(() -> insuranceRequests.markPaymentSuccessful(id, methodName, paid, amount, ref));
    }
}
