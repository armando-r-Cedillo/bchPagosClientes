package com.rocea.batch.pagosclientes.processor;

import com.rocea.batch.pagosclientes.model.Pago;
import com.rocea.batch.pagosclientes.model.PagoProcesado;
import com.rocea.batch.pagosclientes.utilities.Utilidades;
import com.rocea.batch.pagosclientes.utilities.ValidarFecha;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class PagoProcessor implements ItemProcessor<Pago, PagoProcesado> {
    private Log logger = LogFactory.getLog(getClass());

    @Override
    public PagoProcesado process(Pago pago) {

      PagoProcesado pagoProcesado = PagoProcesado.builder()
              .idPago(pago.getIdPago())
              .cuenta(pago.getCuenta())
              .importe(pago.getImporte())
              .fechaPago(pago.getFechaPago())
              .estatus(pago.getEstatus())
              .esFormatoCorrecto(true)
              .detalleError("")
        .build();

        if(pago.getCuenta().length()!=16){
            pagoProcesado.setEsFormatoCorrecto(false);
            pagoProcesado.setDetalleError("Tamaño de cuenta no valido");

        }
        if( pagoProcesado.getImporte().compareTo(BigDecimal.ZERO) <= 0 ){

            pagoProcesado.setEsFormatoCorrecto(false);
            pagoProcesado.setDetalleError(pagoProcesado.getDetalleError().concat("Importe incorrecto"));

        }

        if(!ValidarFecha.validarFormatoFecha(pago.getFechaPago())){

            pagoProcesado.setEsFormatoCorrecto(false);
            pagoProcesado.setDetalleError(pagoProcesado.getDetalleError().concat("Formato de fecha no valido"));
        }

        if(!Utilidades.validarEstatus(pago.getEstatus())){

            pagoProcesado.setEsFormatoCorrecto(false);
            pagoProcesado.setDetalleError(pagoProcesado.getDetalleError().concat("EStatus invalido"));
        }

        return pagoProcesado;
    }
}