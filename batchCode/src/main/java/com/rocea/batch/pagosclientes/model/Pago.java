package com.rocea.batch.pagosclientes.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Pago {

    private Long idPago;
    private String cuenta;
    private BigDecimal importe;
    private String fechaPago;
    private String estatus;
}
