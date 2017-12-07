package tech.lapsa.insurance.daemon.drivenBeans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.time.Instant;
import java.util.Currency;
import java.util.Properties;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import tech.lapsa.epayment.shared.entity.XmlInvoiceHasPaidEvent;
import tech.lapsa.epayment.shared.jms.EpaymentDestinations;
import tech.lapsa.insurance.facade.InsuranceRequestFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.jms.service.JmsReceiverServiceDrivenBean;

@MessageDriven(mappedName = EpaymentDestinations.INVOICE_HAS_PAID)
public class InvoiceHasPaidDrivenBean extends JmsReceiverServiceDrivenBean<XmlInvoiceHasPaidEvent> {

    public InvoiceHasPaidDrivenBean() {
	super(XmlInvoiceHasPaidEvent.class);
    }

    @Override
    public void receiving(XmlInvoiceHasPaidEvent entity, Properties properties) {
	reThrowAsUnchecked(() -> _receiving(entity, properties));
    }

    // PRIVATE

    @Inject
    private InsuranceRequestFacade insuranceRequests;

    private void _receiving(XmlInvoiceHasPaidEvent entity, Properties properties) throws IllegalArgument, IllegalState {
	final String methodName = entity.getMethod();
	final Integer id = Integer.valueOf(entity.getExternalId());
	final Instant paid = entity.getInstant();
	final Double amount = entity.getAmount();
	final Currency currency = entity.getCurrency();
	final String ref = entity.getReferenceNumber();
	insuranceRequests.completePayment(id, methodName, paid, amount, currency, ref);
    }
}
