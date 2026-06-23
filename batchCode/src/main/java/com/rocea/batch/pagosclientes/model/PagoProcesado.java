package com.rocea.batch.pagosclientes.model;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class PagoProcesado {

    private Long idPago;
    private String cuenta;
    private BigDecimal importe;
    private String fechaPago;
    private String estatus;
    private boolean esFormatoCorrecto;
    private String detalleError;

}
