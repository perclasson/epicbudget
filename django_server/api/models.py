from datetime import datetime
from django.contrib.auth.models import User
from django.db.models import CharField, FloatField, Model, DateField, TextField, BooleanField, DateTimeField
from django.db.models.fields.related import ForeignKey

INTERVAL_CHOICES = (
    ('once', 'once'),
    ('monthly', 'monthly'),
)

CATEGORIES = (
    ('expense', 'expense'),
    ('income', 'income'),
)


class Currency(Model):
    name = CharField(max_length=3)
    symbol = CharField(max_length=5)
    is_active = BooleanField(default=True)
    last_modified = DateTimeField(auto_now=True, default=datetime.now())

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "symbol": self.symbol,
            "is_active": self.is_active,
        }

    def __str__(self):
        return self.name

    class Meta:
        verbose_name_plural = u'currencies'


class BudgetUser(User):
    currency = ForeignKey(Currency)
    total_budget = FloatField(null=True, blank=True)


class EntryType(Model):
    name = CharField(max_length=255)
    category = CharField(choices=CATEGORIES, max_length=255)
    last_modified = DateTimeField(auto_now=True, default=datetime.now())
    is_active = BooleanField(default=True)

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "category": self.category,
            "is_active": self.is_active
        }

    def __str__(self):
        return self.category.capitalize() + ": " + self.name


class Budget(Model):
    user = ForeignKey(BudgetUser)
    expense_type = ForeignKey(EntryType)
    amount = FloatField()
    last_modified = DateTimeField(auto_now=True, default=datetime.now())


class Entry(Model):
    user = ForeignKey(BudgetUser)
    amount = FloatField()
    date = DateField()
    description = TextField(blank=True, null=True)
    interval = CharField(choices=INTERVAL_CHOICES, max_length=255)
    entry_type = ForeignKey(EntryType, null=True)
    last_modified = DateTimeField(auto_now=True, default=datetime.now())
    is_active = BooleanField(default=True)

    def to_dict(self):
        return {
            "id": self.id,
            "amount": self.amount,
            "date": str(self.date),
            "interval": self.interval,
            "description": self.description,
            "category": self.entry_type.category,
            "is_active": self.is_active,
            "entry_type": self.entry_type.id
        }
    def __str__(self):
        return str(self.user) + u' ' + str(self.amount)

    class Meta:
        verbose_name_plural = u'entries'