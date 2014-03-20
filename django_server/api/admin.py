from django.contrib import admin
from django.contrib.admin import ModelAdmin
from .models import BudgetUser, IncomeType, ExpenseType, Expense


class BudgetUserAdmin(ModelAdmin):
    pass

class IncomeTypeAdmin(ModelAdmin):
    pass

class ExpenseTypeAdmin(ModelAdmin):
    pass

class ExpenseAdmin(ModelAdmin):
    pass

admin.site.register(BudgetUser, BudgetUserAdmin)
admin.site.register(IncomeType, IncomeTypeAdmin)
admin.site.register(ExpenseType, ExpenseTypeAdmin)
admin.site.register(Expense, ExpenseAdmin)