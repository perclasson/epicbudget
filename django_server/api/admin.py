from django.contrib import admin
from django.contrib.admin import ModelAdmin
from .models import BudgetUser, Entry, EntryType, Currency, Budget


class BudgetUserAdmin(ModelAdmin):
    pass

class EntryTypeAdmin(ModelAdmin):
    def has_delete_permission(self, request, obj=None):
        return False

class EntryAdmin(ModelAdmin):
    pass

class CurrencyAdmin(ModelAdmin):
    pass

class BudgetAdmin(ModelAdmin):
    pass

admin.site.register(BudgetUser, BudgetUserAdmin)
admin.site.register(EntryType, EntryTypeAdmin)
admin.site.register(Entry, EntryAdmin)
admin.site.register(Currency, CurrencyAdmin)
admin.site.register(Budget, BudgetAdmin)