package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private int maximoDepositosDiarios = 3;
  private double maximoExtraccionDiaria = 1000;
  private double saldo = 0;
  private final List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public Cuenta(double montoInicial, int maximoDepositosDiarios, double maximoExtraccionDiaria) {
    this.saldo = montoInicial;
    this.maximoDepositosDiarios = maximoDepositosDiarios;
    this.maximoExtraccionDiaria = maximoExtraccionDiaria;
  }

  public void poner(double cuanto) {
    validarDeposito(cuanto);
    agregarMovimiento(new Deposito(LocalDate.now(), cuanto));
  }

  private void validarDeposito(double cuanto) {
    if (cuanto <= 0)
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");

    if (getCantidadDepositosDiarios(LocalDate.now()) >= maximoDepositosDiarios)
      throw new MaximaCantidadDepositosException("Ya excedio los " + maximoDepositosDiarios + " depositos diarios");
  }

  public void sacar(double cuanto) {
    validarExtraccion(cuanto);
    agregarMovimiento(new Extraccion(LocalDate.now(), cuanto));
  }

  private void validarExtraccion(double cuanto) {
    if (cuanto <= 0)
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    if (cuanto > saldo)
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = maximoExtraccionDiaria - montoExtraidoHoy;
    if (cuanto > limite)
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + maximoExtraccionDiaria + " diarios, límite: " + limite);
  }

  private void agregarMovimiento(Movimiento movimiento) {
    movimientos.add(movimiento);
    saldo = movimiento.modificarSaldo(saldo);
  }

  private long getCantidadDepositosDiarios(LocalDate fecha) {
    return movimientos.stream()
        .filter(movimiento -> movimiento.isDeposito() && movimiento.esDeLaFecha(fecha))
        .count();
  }

  private double getMontoExtraidoA(LocalDate fecha) {
    return movimientos.stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.esDeLaFecha(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }
}
