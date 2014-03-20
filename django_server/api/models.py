from django.contrib.auth.models import User
from django.db.models import CharField, FloatField, Model, DateField, TextField, IntegerField
from django.db.models.fields.related import ForeignKey

CURRENCY_CHOICES = (
    ('SEK', 'SEK'),
    ('EUR', 'EUR'),
    ('USD', 'USD'),
)

INTERVAL_CHOICES = (
    ('once', 'once'),
    ('monthly', 'monthly'),
)

class BudgetUser(User):
    currency = CharField(choices=CURRENCY_CHOICES, max_length=3)
    total_budget = FloatField(null=True, blank=True)


class ExpenseType(Model):
    name = CharField(max_length=255)

    def __str__(self):
        return self.name


class IncomeType(Model):
    name = CharField(max_length=255)

    def __str__(self):
        return self.name


class Budget(Model):
    user = ForeignKey(BudgetUser)
    expense_type = ForeignKey(ExpenseType)
    amount = FloatField()

class Entry(Model):
    user = ForeignKey(BudgetUser)
    amount = FloatField()
    date = DateField()
    description = TextField(blank=True, null=True)
    interval = CharField(choices=INTERVAL_CHOICES, max_length=255)

class Expense(Entry):
    expense_type = ForeignKey(ExpenseType)

class Income(Entry):
    income_type = ForeignKey(IncomeType)
