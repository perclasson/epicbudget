from django.conf.urls import patterns, include, url
from .views import register_view, login_view, add_entry_view


urlpatterns = patterns('',
    url(r'^register/$', register_view, name='register'),
    url(r'^login/$', login_view, name='login'),
    url(r'^add_entry/$', add_entry_view, name='add_entry'),
)