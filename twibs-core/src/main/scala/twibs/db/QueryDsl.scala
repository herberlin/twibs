package twibs.db

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.Tuple1

object QueryDsl {
  implicit def value2tuple[A](x: A) = Tuple1(x)

  def query[C1](c1: Column[C1]): Query[Tuple1[C1]] = new QueryImpl[Tuple1[C1]](List(c1)) {
    override def from(rs: ResultSet, ac: AutoCounter): Tuple1[C1] = new Tuple1(c1.get(rs, ac()))
  }

  def query[C1, C2](c1: Column[C1], c2: Column[C2]): Query[(C1, C2)] = new QueryImpl[(C1, C2)](List(c1, c2)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()))
  }

  def query[C1, C2, C3](c1: Column[C1], c2: Column[C2], c3: Column[C3]): Query[(C1, C2, C3)] = new QueryImpl[(C1, C2, C3)](List(c1, c2, c3)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()))
  }

  def query[C1, C2, C3, C4](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4]): Query[(C1, C2, C3, C4)] = new QueryImpl[(C1, C2, C3, C4)](List(c1, c2, c3, c4)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5]): Query[(C1, C2, C3, C4, C5)] = new QueryImpl[(C1, C2, C3, C4, C5)](List(c1, c2, c3, c4, c5)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6]): Query[(C1, C2, C3, C4, C5, C6)] = new QueryImpl[(C1, C2, C3, C4, C5, C6)](List(c1, c2, c3, c4, c5, c6)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7]): Query[(C1, C2, C3, C4, C5, C6, C7)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7)](List(c1, c2, c3, c4, c5, c6, c7)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8]): Query[(C1, C2, C3, C4, C5, C6, C7, C8)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8)](List(c1, c2, c3, c4, c5, c6, c7, c8)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()), c11.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()), c11.get(rs, ac()), c12.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()), c11.get(rs, ac()), c12.get(rs, ac()), c13.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()), c11.get(rs, ac()), c12.get(rs, ac()), c13.get(rs, ac()), c14.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()), c11.get(rs, ac()), c12.get(rs, ac()), c13.get(rs, ac()), c14.get(rs, ac()), c15.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()), c11.get(rs, ac()), c12.get(rs, ac()), c13.get(rs, ac()), c14.get(rs, ac()), c15.get(rs, ac()), c16.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16], c17: Column[C17]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()), c11.get(rs, ac()), c12.get(rs, ac()), c13.get(rs, ac()), c14.get(rs, ac()), c15.get(rs, ac()), c16.get(rs, ac()), c17.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16], c17: Column[C17], c18: Column[C18]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()), c11.get(rs, ac()), c12.get(rs, ac()), c13.get(rs, ac()), c14.get(rs, ac()), c15.get(rs, ac()), c16.get(rs, ac()), c17.get(rs, ac()), c18.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16], c17: Column[C17], c18: Column[C18], c19: Column[C19]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()), c11.get(rs, ac()), c12.get(rs, ac()), c13.get(rs, ac()), c14.get(rs, ac()), c15.get(rs, ac()), c16.get(rs, ac()), c17.get(rs, ac()), c18.get(rs, ac()), c19.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16], c17: Column[C17], c18: Column[C18], c19: Column[C19], c20: Column[C20]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()), c11.get(rs, ac()), c12.get(rs, ac()), c13.get(rs, ac()), c14.get(rs, ac()), c15.get(rs, ac()), c16.get(rs, ac()), c17.get(rs, ac()), c18.get(rs, ac()), c19.get(rs, ac()), c20.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, C21](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16], c17: Column[C17], c18: Column[C18], c19: Column[C19], c20: Column[C20], c21: Column[C21]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, C21)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, C21)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()), c11.get(rs, ac()), c12.get(rs, ac()), c13.get(rs, ac()), c14.get(rs, ac()), c15.get(rs, ac()), c16.get(rs, ac()), c17.get(rs, ac()), c18.get(rs, ac()), c19.get(rs, ac()), c20.get(rs, ac()), c21.get(rs, ac()))
  }

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, C21, C22](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16], c17: Column[C17], c18: Column[C18], c19: Column[C19], c20: Column[C20], c21: Column[C21], c22: Column[C22]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, C21, C22)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, C21, C22)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22)) {
    override def from(rs: ResultSet, ac: AutoCounter) = (c1.get(rs, ac()), c2.get(rs, ac()), c3.get(rs, ac()), c4.get(rs, ac()), c5.get(rs, ac()), c6.get(rs, ac()), c7.get(rs, ac()), c8.get(rs, ac()), c9.get(rs, ac()), c10.get(rs, ac()), c11.get(rs, ac()), c12.get(rs, ac()), c13.get(rs, ac()), c14.get(rs, ac()), c15.get(rs, ac()), c16.get(rs, ac()), c17.get(rs, ac()), c18.get(rs, ac()), c19.get(rs, ac()), c20.get(rs, ac()), c21.get(rs, ac()), c22.get(rs, ac()))
  }

  def deleteFrom(table: Table): DeleteFrom = new DeleteFromImpl(table)

  private class QueryImpl[T <: Product](val columns: List[Column[_]], val where: Where = EmptyWhere, val orderBy: OrderBy = EmptyOrderBy) extends Query[T] {
    self =>

    // TODO remove after fix of IntelliJ: http://youtrack.jetbrains.com/issue/SCL-7062
    override def from(rs: ResultSet, ac: AutoCounter): T = throw new NotImplementedError

    private val columnsWithIndex = columns.zipWithIndex

    private val tables = columns.map(_.table).distinct

    def to(ps: PreparedStatement, t: T): Unit = t.productIterator.zip(columnsWithIndex.iterator) map { case (value, (column, index)) => column.set(ps, index, value)}

    override def where(whereArg: Where): Query[T] = new QueryImpl[T](columns, where && whereArg, orderBy) {
      override def from(rs: ResultSet, ac: AutoCounter): T = self.from(rs, ac)
    }

    override def orderBy(orderByArg: OrderBy): Query[T] = new QueryImpl[T](columns, where, orderBy ++ orderByArg) {
      override def from(rs: ResultSet, ac: AutoCounter): T = self.from(rs, ac)
    }

    override def also[R <: Product](right: Query[R]): Query[(T, R)] = new QueryImpl[(T, R)](columns ::: right.columns, where, orderBy) {
      override def from(rs: ResultSet, ac: AutoCounter): (T, R) = (self.from(rs, ac), right.from(rs, ac))
    }

    override def insertAndReturn[R](values: T)(column: Column[R])(implicit connection: Connection): R = insertStatement(values).insertAndReturn(connection)(column)

    override def insert(values: T)(implicit connection: Connection): Unit = insertStatement(values).insert(connection)

    private def insertStatement(values: T) = Statement(toInsertSql, columnParameters(values))

    private def columnParameters(values: T) = columns.zip(values.productIterator.toList)

    def select(implicit connection: Connection): TraversableOnce[T] with AutoCloseable = new Iterator[T] with AutoCloseable {
      private val rs: ResultSet = selectStatement.select(connection)

      private var closed = false

      private var lookAhead = rs.next()

      override def hasNext: Boolean = {
        if (lookAhead) true
        else {
          close()
          false
        }
      }

      override def next(): T = {
        val ret = from(rs, new AutoCounter)
        lookAhead = rs.next()
        ret
      }

      override def close(): Unit = {
        if (!closed) {
          val stmt = rs.getStatement
          rs.close()
          stmt.close()
          closed = true
        }
      }
    }

    private def selectStatement = Statement(s"SELECT $columnListSql FROM $tableListSql", Nil) ~ where.toStatement ~ orderBy.toStatement

    override def update(values: T)(implicit connection: Connection): Long = updateStatement(values).update(connection)

    private def updateStatement(values: T) = Statement(toUpdateSql, columnParameters(values)) ~ where.toStatement

    def size(implicit connection: Connection): Long = sizeStatement.size(connection)

    private def sizeStatement = Statement(s"SELECT count(*) FROM $tableListSql") ~ where.toStatement

    private def columnListSql = columns.map(_.fullName).mkString(",")

    private def tableListSql = tables.map(_.tableName).mkString(",")

    //// Debugging information ////
    override def toSelectSql: String = selectStatement.sql

    override def toUpdateSql: String = s"UPDATE $tableListSql SET ${columns.map(_.fullName + " = ?").mkString(",")}"

    override def toInsertSql: String = s"INSERT INTO $tableListSql(${columns.map(_.name).mkString(",")}) VALUES(${columns.map(x => "?").mkString(",")})"
  }

  private class DeleteFromImpl(table: Table, where: Where = EmptyWhere) extends DeleteFrom {
    override def where(whereArg: Where): DeleteFrom = new DeleteFromImpl(table, where && whereArg)

    override def delete(implicit connection: Connection): Long = toStatement.update(connection)

    private def toStatement = Statement(s"DELETE FROM ${table.tableName}") ~ where.toStatement

    //// Debugging information ////
    override def toDeleteSql: String = toStatement.sql
  }

  private object EmptyWhere extends Where {
    override def &&(right: Where) = new Where {
      override private[db] def toStatement: Statement = Statement(" WHERE ") ~ right.toStatement

      override private[db] def toStatement(parentPrecedence: Int): Statement = right.toStatement(parentPrecedence)
    }

    override private[db] def toStatement: Statement = EmptyStatement
  }

  private object EmptyOrderBy extends OrderBy {
    override def ++(right: OrderBy) = new OrderBy {
      override private[db] def toStatement: Statement = Statement(" ORDER BY ") ~ right.toStatement
    }

    override private[db] def toStatement: Statement = EmptyStatement
  }

  private object EmptyStatement extends Statement("", Nil)

}
