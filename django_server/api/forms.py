from django.forms import ModelForm
from .models import BudgetUser, Entry


class BudgetUserForm(ModelForm):
    class Meta:
        model = BudgetUser
        exclude = ['last_login', 'date_joined',]

    def save(self, commit=True):
        user = super(BudgetUserForm, self).save(commit=False)
        user.set_password(self.cleaned_data["password"])
        if commit:
            user.save()
        return user

class EntryForm(ModelForm):
    class Meta:
        model = Entry
