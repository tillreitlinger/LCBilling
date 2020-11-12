case class Outlay(payedFrom: Option[Person], payedFor: Option[Seq[Person]], amount: Option[Float], at: Option[Place]){
  def from(person: Person)={
    copy(Some(person), payedFor, amount, at)
  }
  def towards(persons: Person*)={
    copy(payedFrom, Some(persons), amount, at)
  }
  def of(amount: Float) ={
    copy(payedFrom, payedFor, Some(amount), at)
  }
  def at(place: Place) ={
    copy(payedFrom, payedFor, amount, Some(place))
  }
}
