package scala

object implicitFunctions{
  class currency(n:Float){
    def EUR = n
    def CHF = (n*0.93).toFloat
  }

  implicit def currencyConversion(n:Float)= new currency(n)
}
