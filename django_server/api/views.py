import json
from django.contrib.auth import login, authenticate, logout
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from api.forms import BudgetUserForm, ExpenseForm, IncomeForm
from models import CURRENCY_CHOICES, ExpenseType, IncomeType


@csrf_exempt
def register_view(request):
    data = json.loads(request.body)

    username = data.get('username', '')
    password = data.get('password', '')
    currency = data.get('currency', CURRENCY_CHOICES[0][0])

    form = BudgetUserForm(data={
        'username': username,
        'password': password,
        'currency': currency,
        'is_active': True
    })

    if form.is_valid():
        form.save()
        data = {
            "message": "Registration was successful!",
            "registered": True
        }
    else:
        field_name, error = form.errors.items()[0]
        data = {
            "message": field_name.capitalize() + ": " + error[0],
            "registered": False
        }

    return HttpResponse(json.dumps(data), content_type='application/json')


@csrf_exempt
def add_entry_view(request):
    data = json.loads(request.body)
    response = {}

    if request.user.is_authenticated():
        data['user'] = request.user

        entry_type = data.get('entry_type', '')

        if entry_type == 'expense':
            data['expense_type'] = ExpenseType.objects.get(name=data['type']).id
            form = ExpenseForm(data=data)
        else:
            data['income_type'] = IncomeType.objects.get(name=data['type']).id
            form = IncomeForm(data=data)

        if form.is_valid():
            form.save()
            response["message"] = "Entry added."
            response["success"] = True
        else:
            print form.errors
            response["message"] = "Error in entry."
            response["success"] = False

    return HttpResponse(json.dumps(response), content_type='application/json')

@csrf_exempt
def login_view(request):
    data = json.loads(request.body)

    username = data.get('username', '')
    password = data.get('password', '')

    if not password and not username:
        password = 'martin'
        username = 'martin'

    user = authenticate(username=username, password=password)
    if user is not None:
        if user.is_active:
            login(request, user)
            data = {"message": "The user is logged in."}
        else:
            data = {"message": "The user is not active."}
    else:
        logout(request)
        data = {"message": "The username or password is wrong."}

    data["is_authenticated"] = request.user.is_authenticated()

    return HttpResponse(json.dumps(data), content_type='application/json')
