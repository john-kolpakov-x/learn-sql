package kz.pompei.learn.sql.preparation

enum class TransactionType(val description: String, val refill: Boolean) {
  INITIAL_POPULATE("Начальное пополнение средств на счёт", true),
  SALE_GOODS("Продажа товара", true),
  BUY_GOODS("Покупка товара", false),
  SALE_SERVICE("Получение вознаграждения за устлугу", true),
  BUY_SERVICE("Оплата услуги", false),
  INCOME_TAX("Подоходний налог", false),
}
