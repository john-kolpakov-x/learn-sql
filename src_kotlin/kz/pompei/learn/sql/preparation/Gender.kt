package kz.pompei.learn.sql.preparation

import java.util.*

enum class Gender {
  MALE {
    override fun rndSurname(res: GenResources, rnd: Random): String {
      return res.surnamesMen[rnd.nextInt(res.surnamesMen.size)]
    }

    override fun rndName(res: GenResources, rnd: Random): String {
      return res.namesMen[rnd.nextInt(res.namesMen.size)]
    }

    override fun rndPatronymic(res: GenResources, rnd: Random): String {
      return res.patronymicsMen[rnd.nextInt(res.patronymicsMen.size)]
    }

  },

  FEMALE {
    override fun rndSurname(res: GenResources, rnd: Random): String {
      return res.surnamesWomen[rnd.nextInt(res.surnamesWomen.size)]
    }

    override fun rndName(res: GenResources, rnd: Random): String {
      return res.namesWomen[rnd.nextInt(res.namesWomen.size)]
    }

    override fun rndPatronymic(res: GenResources, rnd: Random): String {
      return res.patronymicsWomen[rnd.nextInt(res.patronymicsWomen.size)]
    }
  };

  abstract fun rndSurname(res: GenResources, rnd: Random): String
  abstract fun rndName(res: GenResources, rnd: Random): String
  abstract fun rndPatronymic(res: GenResources, rnd: Random): String
}