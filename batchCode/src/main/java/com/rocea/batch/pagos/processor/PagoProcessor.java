package com.rocea.batch.pagos.processor;
import com.rocea.batch.pagos.model.Pago;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class PagoProcessor implements ItemProcessor<Pago, Pago> {

    @Override
    public Pago process(Pago pago) {

        pago.setEstatus(pago.getEstatus().toUpperCase());

        return pago;
    }
}