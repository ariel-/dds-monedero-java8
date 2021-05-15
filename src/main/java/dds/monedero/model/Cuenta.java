package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Cuenta {

  private final List<Movimiento> movimientos = new ArrayList<>();
  private int maximoDepositosDiarios = 3;
  private double maximoExtraccionDiaria = 1000;
  private double saldo = 0;

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
    hacerOperacion(cuanto, () ->
    {
      validarMaximoDepositos();
      return new Deposito(LocalDate.now(), cuanto);
    });
  }

  public void sacar(double cuanto) {
    hacerOperacion(cuanto, () ->
    {
      validarSaldoRestante(cuanto);
      validarLimite(cuanto);
      return new Extraccion(LocalDate.now(), cuanto);
    });
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  private void hacerOperacion(double cuanto, Supplier<Movimiento> validarYObtenerMovimiento) {
    validarMontoPositivo(cuanto);
    Movimiento movimiento = validarYObtenerMovimiento.get();
    agregarMovimiento(movimiento);
  }

  private void validarMaximoDepositos() {
    if (getCantidadDepositosDiarios(LocalDate.now()) >= maximoDepositosDiarios)
      throw new MaximaCantidadDepositosException("Ya excedio los " + maximoDepositosDiarios + " depositos diarios");
  }

  private void validarMontoPositivo(double cuanto) {
    if (cuanto <= 0)
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
  }

  private void validarLimite(double cuanto) {
    double limite = maximoExtraccionDiaria - getMontoExtraidoA(LocalDate.now());
    if (cuanto > limite)
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + maximoExtraccionDiaria + " diarios, límite: " + limite);
  }

  private void validarSaldoRestante(double cuanto) {
    if (cuanto > saldo)
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
  }

  private void agregarMovimiento(Movimiento movimiento) {
    movimientos.add(movimiento);
    saldo = movimiento.modificarSaldo(saldo);
  }

  private long getCantidadDepositosDiarios(LocalDate fecha) {
    return movimientos.stream()
                      .filter(movimiento -> movimiento.fueDepositado(fecha))
                      .count();
  }

  private double getMontoExtraidoA(LocalDate fecha) {
    return movimientos.stream()
                      .filter(movimiento -> movimiento.fueExtraido(fecha))
                      .mapToDouble(Movimiento::getMonto)
                      .sum();
  }
}
