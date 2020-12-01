
//class Source {
//  val linesFromTXT: Source[String, NotUsed] = FileIO.fromPath(Paths.get("a.csv"))
//    .via(Framing.delimiter(ByteString("\n"), 256, true).map(_.utf8String))
//    .to(sink)
//    .run()
//}
