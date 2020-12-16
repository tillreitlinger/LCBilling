package scala

class OutlayData {
  private var payedFrom:String = ""
  private var payedFor:Array[String] = Array()
  private var amount:Double = 0.0
  private var payedAt:String = ""


  def this(payedFrom: String, payedFor:Array[String], amount:Double, payedAt:String) {
    this()
    this.payedFrom = payedFrom
    this.payedFor = payedFor
    this.amount = amount
    this.payedAt = payedAt
  }

  def getPayedFrom: String = this.payedFrom
  def getPayedFor: Array[String] = this.payedFor
  def getAmount: Double = this.amount
  def getPayedAt: String = this.payedAt
}
