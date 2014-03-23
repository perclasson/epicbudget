from django.conf.urls import patterns, include, url
from .views import register_view, login_view, add_entry_view, entries_view,\
    currencies_view, entry_types_view, delete_entry_view


urlpatterns = patterns('',
    url(r'^register/$', register_view, name='register'),
    url(r'^login/$', login_view, name='login'),
    url(r'^add_entry/$', add_entry_view, name='add_entry'),
    url(r'^entry_types/$', entry_types_view, name='entry_types'),
    url(r'^currencies/$', currencies_view, name='currencies'),
    url(r'^entries/$', entries_view, name='entries'),
    url(r'^delete_entry/$', delete_entry_view, name='delete_entry'),
)