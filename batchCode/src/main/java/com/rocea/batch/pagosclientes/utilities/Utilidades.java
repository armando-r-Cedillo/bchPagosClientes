package com.rocea.batch.pagosclientes.utilities;

public  class Utilidades {


   public static boolean validarEstatus(String estatus) {


       if (estatus == null) {
           return false;
       }

       try{
           ConstantesEstatus constantesEstatus = ConstantesEstatus.valueOf(estatus);

           return switch (constantesEstatus) {
               case ConstantesEstatus.APROBADO,
                    ConstantesEstatus.PENDIENTE,
                    ConstantesEstatus.RECHAZADO -> true;

               default -> false;
           };
       } catch (IllegalArgumentException e) {
           return false;
       }


   }

}
