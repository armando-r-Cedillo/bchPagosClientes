package com.carga.cliente.utilidades;

import com.carga.cliente.utilities.ValidarFecha;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ValidarFechaTest {

    private static Log logger = LogFactory.getLog(ValidarFecha.class);


    @Test
     void validarFormatoFecha(){

        logger.info("Iniciando prueba unitaria para validar formato Fecha");
        boolean resultado = ValidarFecha.validarFormatoFecha("1994-10-22");

        // Assert
        assertTrue(resultado);
    }

    @Test
    void validarFormatoFechaInvalido(){
        logger.info("Iniciando prueba unitaria para validar formato Fecha");
        boolean resultado = ValidarFecha.validarFormatoFecha("30-12-1822");

        // Assert
        assertFalse(resultado);

    }

    @Test
     void validarFormatoFechaMalformato(){
        logger.info("Iniciando prueba unitaria para validar formato Fecha");
        boolean resultado = ValidarFecha.validarFormatoFecha("1010-1922");

        // Assert
        assertFalse(resultado);

    }

}
