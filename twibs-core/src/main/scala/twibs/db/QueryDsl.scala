package twibs.db

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.Tuple1
import twibs.util.SortOrder
import twibs.util.SortOrder.SortOrder

object QueryDsl {
  implicit def value2tuple[A](x: A) = Tuple1(x)

  def makeFlat(v: Product): List[Any] = v.productIterator.toList.flatten { case p: Option[_] => p :: Nil; case p: Product => makeFlat(p); case a => a :: Nil}

  def query[C1](c1: Column[C1]): Query[Tuple1[C1]] = new QueryImpl[Tuple1[C1]](List(c1), from = (rs, ac) => new Tuple1(c1.sget(rs, ac())))

  def query[C1, C2](c1: Column[C1], c2: Column[C2]): Query[(C1, C2)] = new QueryImpl[(C1, C2)](List(c1, c2),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()))
  )

  def query[C1, C2, C3](c1: Column[C1], c2: Column[C2], c3: Column[C3]): Query[(C1, C2, C3)] = new QueryImpl[(C1, C2, C3)](List(c1, c2, c3),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4]): Query[(C1, C2, C3, C4)] = new QueryImpl[(C1, C2, C3, C4)](List(c1, c2, c3, c4),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5]): Query[(C1, C2, C3, C4, C5)] = new QueryImpl[(C1, C2, C3, C4, C5)](List(c1, c2, c3, c4, c5),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6]): Query[(C1, C2, C3, C4, C5, C6)] = new QueryImpl[(C1, C2, C3, C4, C5, C6)](List(c1, c2, c3, c4, c5, c6),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7]): Query[(C1, C2, C3, C4, C5, C6, C7)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7)](List(c1, c2, c3, c4, c5, c6, c7),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8]): Query[(C1, C2, C3, C4, C5, C6, C7, C8)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8)](List(c1, c2, c3, c4, c5, c6, c7, c8),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()), c11.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()), c11.sget(rs, ac()), c12.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()), c11.sget(rs, ac()), c12.sget(rs, ac()), c13.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()), c11.sget(rs, ac()), c12.sget(rs, ac()), c13.sget(rs, ac()), c14.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()), c11.sget(rs, ac()), c12.sget(rs, ac()), c13.sget(rs, ac()), c14.sget(rs, ac()), c15.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()), c11.sget(rs, ac()), c12.sget(rs, ac()), c13.sget(rs, ac()), c14.sget(rs, ac()), c15.sget(rs, ac()), c16.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16], c17: Column[C17]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()), c11.sget(rs, ac()), c12.sget(rs, ac()), c13.sget(rs, ac()), c14.sget(rs, ac()), c15.sget(rs, ac()), c16.sget(rs, ac()), c17.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16], c17: Column[C17], c18: Column[C18]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()), c11.sget(rs, ac()), c12.sget(rs, ac()), c13.sget(rs, ac()), c14.sget(rs, ac()), c15.sget(rs, ac()), c16.sget(rs, ac()), c17.sget(rs, ac()), c18.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16], c17: Column[C17], c18: Column[C18], c19: Column[C19]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()), c11.sget(rs, ac()), c12.sget(rs, ac()), c13.sget(rs, ac()), c14.sget(rs, ac()), c15.sget(rs, ac()), c16.sget(rs, ac()), c17.sget(rs, ac()), c18.sget(rs, ac()), c19.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16], c17: Column[C17], c18: Column[C18], c19: Column[C19], c20: Column[C20]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()), c11.sget(rs, ac()), c12.sget(rs, ac()), c13.sget(rs, ac()), c14.sget(rs, ac()), c15.sget(rs, ac()), c16.sget(rs, ac()), c17.sget(rs, ac()), c18.sget(rs, ac()), c19.sget(rs, ac()), c20.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, C21](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16], c17: Column[C17], c18: Column[C18], c19: Column[C19], c20: Column[C20], c21: Column[C21]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, C21)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, C21)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()), c11.sget(rs, ac()), c12.sget(rs, ac()), c13.sget(rs, ac()), c14.sget(rs, ac()), c15.sget(rs, ac()), c16.sget(rs, ac()), c17.sget(rs, ac()), c18.sget(rs, ac()), c19.sget(rs, ac()), c20.sget(rs, ac()), c21.sget(rs, ac()))
  )

  def query[C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, C21, C22](c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7], c8: Column[C8], c9: Column[C9], c10: Column[C10], c11: Column[C11], c12: Column[C12], c13: Column[C13], c14: Column[C14], c15: Column[C15], c16: Column[C16], c17: Column[C17], c18: Column[C18], c19: Column[C19], c20: Column[C20], c21: Column[C21], c22: Column[C22]): Query[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, C21, C22)] = new QueryImpl[(C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, C21, C22)](List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22),
    from = (rs, ac) => (c1.sget(rs, ac()), c2.sget(rs, ac()), c3.sget(rs, ac()), c4.sget(rs, ac()), c5.sget(rs, ac()), c6.sget(rs, ac()), c7.sget(rs, ac()), c8.sget(rs, ac()), c9.sget(rs, ac()), c10.sget(rs, ac()), c11.sget(rs, ac()), c12.sget(rs, ac()), c13.sget(rs, ac()), c14.sget(rs, ac()), c15.sget(rs, ac()), c16.sget(rs, ac()), c17.sget(rs, ac()), c18.sget(rs, ac()), c19.sget(rs, ac()), c20.sget(rs, ac()), c21.sget(rs, ac()), c22.sget(rs, ac()))
  )

  def deleteFrom(table: Table): DeleteFrom = new DeleteFromImpl(table)

  private case class QueryImpl[T <: Product](columns: List[Column[_]], where: Where = EmptyWhere, orderBy: OrderBy = EmptyOrderBy, groupBy: List[Column[_]] = Nil, joins: List[(Column[_], Column[_])] = Nil, offset: Long = 0L, limit: Long = Long.MaxValue, isDistinct: Boolean = false, from: (ResultSet, AutoCounter) => T = null) extends Query[T] {
    self =>
    private val columnsWithIndex = columns.zipWithIndex

    private val tables = (joins.map(_._1.table) ::: columns.map(_.table)).distinct.diff(joinedTables)

    def joinedTables = joins.map(_._2.table).distinct

    def to(ps: PreparedStatement, values: T): Unit = makeFlat(values).zip(columnsWithIndex) map { case (value, (column, index)) => column.set(ps, index, value)}

    override def where(whereArg: Where): Query[T] = copy(where = where && whereArg)

    override def groupBy(column: Column[_]): Query[T] = copy(groupBy = column :: groupBy)

    override def join(left: Column[_], right: Column[_]): Query[T] = copy(joins = (left, right) :: joins)

    override def offset(offset: Long): Query[T] = copy(offset = offset)

    override def limit(limit: Long): Query[T] = copy(limit = limit)

    override def distinct: Query[T] = copy(isDistinct = true)

    override def orderBy(orderByArg: OrderBy): Query[T] = copy(orderBy = orderBy ++ orderByArg)

    override def orderBy(orderBys: List[OrderBy]): Query[T] = orderBys match {
      case Nil => this
      case l => orderBy(l.head).orderBy(l.tail)
    }

    override def orderByName(orderBys: List[(String, SortOrder)]): Query[T] =
      orderBy(orderBys.map { case (name, sort) =>
        columnForName(name).flatMap(column =>
            sort match {
              case SortOrder.Ascending => Some(column.asc)
              case SortOrder.Descending => Some(column.desc)
              case _ => None
            }
        )
      }.flatten)

    def columnForName(columnName: String) = columns.find(_.name == columnName)

    override def also[R <: Product](right: Query[R]): Query[(T, R)] = new QueryImpl[(T, R)](columns ::: right.columns, where, orderBy, groupBy, joins, offset, limit, isDistinct,
      (rs, ac) => (self.from(rs, ac), right.asInstanceOf[QueryImpl[R]].from(rs, ac))
    )

    override def insertAndReturn[R](values: T)(column: Column[R])(implicit connection: Connection): R = insertStatement(values).insertAndReturn(connection)(column)

    override def insert(values: T)(implicit connection: Connection): Unit = insertStatement(values).insert(connection)

    private def insertStatement(values: T) = Statement(toInsertSql, columnParameters(values))

    private def columnParameters(values: T) = columns.zip(makeFlat(values))

    def select(implicit connection: Connection): Iterator[T] with AutoCloseable = new Iterator[T] with AutoCloseable {
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

    private def selectStatement = sizeOrSelectStatement ~ orderBy.toStatement ~ offsetStatement ~ limitStatement

    private def sizeOrSelectStatement = Statement(s"SELECT${if (isDistinct) " DISTINCT" else ""} $columnListSql FROM $tableListSql", Nil) ~ where.toStatement ~ joinStatement ~ groupByStatement

    private def joinStatement = Statement(joins.map { case (l, r) => s" JOIN ${r.table.tableName} ON ${r.fullName} = ${l.fullName}"}.mkString)

    private def limitStatement = if (limit < Long.MaxValue) Statement(s" LIMIT $limit") else EmptyStatement

    private def offsetStatement = if (offset > 0) Statement(s" OFFSET $offset") else EmptyStatement

    private def groupByStatement = groupBy match {
      case Nil => EmptyStatement
      case l => Statement(s" GROUP BY ${l.map(_.fullName).mkString(",")}")
    }

    override def update(values: T)(implicit connection: Connection): Long = updateStatement(values).update(connection)

    private def updateStatement(values: T) = Statement(toUpdateSql, columnParameters(values)) ~ where.toStatement

    def size(implicit connection: Connection): Long = sizeStatement.size(connection)

    private def sizeStatement =
      if (isDistinct)
        Statement(s"SELECT count(*) FROM (") ~ sizeOrSelectStatement ~ Statement(") AS count")
      else
        Statement(s"SELECT count(*) FROM $tableListSql") ~ where.toStatement

    private def columnListSql = columns.map(_.fullName).mkString(",")

    private def tableListSql = tables.map(_.tableName).mkString(",")

    //// Debugging information ////
    override def toSelectSql: String = selectStatement.sql

    override def toUpdateSql: String = s"UPDATE $tableListSql SET ${columns.map(_.name + "=?").mkString(",")}"

    override def toInsertSql: String = s"INSERT INTO $tableListSql(${columns.map(_.name).mkString(",")}) VALUES(${columns.map(x => "?").mkString(",")})"

    override def convert[R <: Product](convertTo: (T) => R, convertFrom: (R) => Option[T]): Query[R] =
      new QueryImpl[R](columns, where, orderBy, groupBy, joins, offset, limit, isDistinct,
        (rs, ac) => convertTo(self.from(rs, ac))) {
        override def to(ps: PreparedStatement, values: R): Unit = self.to(ps, convertFrom(values).get)
      }
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

      override private[db] def toStatement(parentPrecedence: Int): Statement = Statement(" WHERE ") ~ right.toStatement(parentPrecedence)
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
