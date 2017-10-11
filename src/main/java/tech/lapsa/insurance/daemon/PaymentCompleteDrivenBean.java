package tech.lapsa.insurance.daemon;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import tech.lapsa.epayment.facade.Ebill;
import tech.lapsa.epayment.facade.beans.EpaymentFacadeBean;
import tech.lapsa.insurance.facade.InsuranceRequestFacade;
import tech.lapsa.javax.jms.ObjectConsumerListener;

@MessageDriven(mappedName = PaymentCompleteDrivenBean.JNDI_JMS_DEST)
public class PaymentCompleteDrivenBean extends ObjectConsumerListener<Ebill> {

    public PaymentCompleteDrivenBean() {
	super(Ebill.class);
    }

    public static final String JNDI_JMS_DEST = EpaymentFacadeBean.JNDI_JMS_DEST_PAID_EBILLs;

    @Inject
    private InsuranceRequestFacade facade;

    @Override
    public void accept(Ebill object) {
	facade.markPaymentComplete(Integer.valueOf(object.getExternalId()), object.getReference(), object.getPaid());
    }
}
