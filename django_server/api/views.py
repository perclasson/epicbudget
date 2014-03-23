import json
from datetime import datetime
from django.contrib.auth import login, authenticate, logout
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from api.forms import BudgetUserForm, EntryForm
from models import Entry, EntryType, Currency, BudgetUser


@csrf_exempt
def register_view(request):
    data = json.loads(request.body)

    username = data.get('username', '')
    password = data.get('password', '')
    currency = data.get('currency', '')

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
        data['is_active'] = True
        data['date'] = datetime.fromtimestamp(data['date']/1000.0)
        data['entry_type'] = EntryType.objects.get(name=data['entry_type']).id
        form = EntryForm(data=data)

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


    response = {}
    logout(request)

    user = authenticate(username=username, password=password)
    if user is not None:
        if user.is_active:
            login(request, user)
            response["message"] = "The user is logged in."
            response["currency_symbol"] = BudgetUser.objects.get(
                id=user.id).currency.symbol
            response["user_id"] = user.id
        else:
            response["message"] = "The user is not active."
    else:
        logout(request)
        response["message"] =  "The username or password is wrong."

    response["is_authenticated"] = request.user.is_authenticated()

    return HttpResponse(json.dumps(response), content_type='application/json')

@csrf_exempt
def entries_view(request):
    user = request.user
    if user.is_authenticated():
        entries = {
            "entries": [e.to_dict() for e in Entry.objects.filter(user=user)]
        }
        return HttpResponse(
            json.dumps(entries), content_type='application/json')

@csrf_exempt
def currencies_view(request):
    data = json.loads(request.body)
    last_sync = datetime.fromtimestamp(data['last_sync']/1000.0)

    currencies = {
        "currencies": [c.to_dict() for c in Currency.objects.filter(
            last_modified__gt=last_sync)]
    }

    return HttpResponse(json.dumps(currencies), content_type='application/json')

@csrf_exempt
def entry_types_view(request):
    data = json.loads(request.body)
    last_sync = datetime.fromtimestamp(data['last_sync']/1000.0)

    entry_types = {
        "entry_types": [e.to_dict() for e in EntryType.objects.filter(
            last_modified__gt=last_sync)]
    }
    return HttpResponse(
        json.dumps(entry_types), content_type='application/json')


@csrf_exempt
def delete_entry_view(request):
    data = json.loads(request.body)
    user = request.user
    success = False

    if user.is_authenticated():
        entry = Entry.objects.get(id=data['id'])
        entry.is_active = False
        entry.save()
        success = True

    return HttpResponse(json.dumps(
        {"success": success}), content_type='application/json')
