## Monedero

### Contexto

Este repositorio contiene el código de un _monedero virtual_, al que podemos agregarle y quitarle dinero, a través 
de los métodos `Monedero.sacar` y `Monedero.poner`, respectivamente. 
Pero hay algunos problemas: por un lado el código no está muy bien testeado, y por el otro, hay numeros _code smells_. 

### Consigna

Tenés seis tareas: 

 1. :fork_and_knife: Hacé un _fork_ de este repositorio (presionando desde Github el botón Fork)
 2. :arrow_down: Descargalo y construí el proyecto, utilizando `maven`
 2. :nose: Identificá y anotá todos los _code smells_ que encuentres 
 3. :test_tube: Agregá los tests faltantes y mejorá los existentes. 
     * :eyes: Ojo: ¡un test sin ningún tipo de aserción está incompleto!
 4. :rescue_worker_helmet: Corregí smells, de a un commit por vez. 
 5. :arrow_up: Subí todos los cambios a tu _fork_
 6. :bug: Activá los issues de Github desde https://github.com/TU_GITHUB_USERNAME/dds-monedero-java8/settings así podemos darte nuestras devoluciones

### Tecnologías usadas

* Java 8.
* JUnit 5. :warning: La versión 5 de JUnit es la más nueva del framework y presenta algunas diferencias respecto a la versión "clásica" (JUnit 4). Para mayores detalles, ver:
    *  [Apunte de herramientas](https://docs.google.com/document/d/1VYBey56M0UU6C0689hAClAvF9ILE6E7nKIuOqrRJnWQ/edit#heading=h.dnwhvummp994)
    *  [Entrada de Blog (en inglés)](https://www.baeldung.com/junit-5-migration)
    *  [Entrada de Blog (en español)](https://www.paradigmadigital.com/dev/nos-espera-junit-5/)
* Maven 3.3 o superior
 
### Resolución

#### Smells detectados
* No tiene sentido setear los movimientos en una Cuenta, si se diera el caso, estoy creando una nueva Cuenta.
* Type Check en Movimiento: Siempre voy a necesitar saber si es un depósito o una extracción. La solución propuesta es modelar dichas entidades por separado, que compartan una misma interfaz
* El movimiento conoce a la Cuenta y la Cuenta al movimiento. Una de las dos es innecesaria y complica la interfaz. La cuenta sabe cómo agregar un movimiento, ya sea depósito o extracción, y el movimiento envía a la cuenta el mensaje de agregarse (sin enviarse a sí mismo! -usando this-) y además _setea_ el saldo (ver siguiente). Por todo esto, decidí que lo mejor es eliminar el conocimiento de la Cuenta en el Movimiento.
* El Saldo puede *setearse* por fuera de los mensajes de poner y sacar dinero, esto rompe con el encapsulamiento y además deja la clase en un estado inválido, en el cual el saldo no se condice con los movimientos (trazabilidad deseable). La solución propuesta es dejar el saldo a modo de _cache_, para no tener que calcularlo cada vez que preguntan por el saldo.
