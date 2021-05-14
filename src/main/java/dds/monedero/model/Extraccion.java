package dds.monedero.model;

import java.time.LocalDate;

public class Extraccion extends Movimiento {
  public Extraccion(LocalDate fecha, double monto) {
    super(fecha, monto);
  }

  @Override
  public double modificarSaldo(double saldo) {
    return saldo - this.getMonto();
  }

  @Override
  public boolean isDeposito() {
    return false;
  }
}
