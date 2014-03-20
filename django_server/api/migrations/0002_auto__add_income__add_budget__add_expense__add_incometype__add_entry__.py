# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'Income'
        db.create_table(u'api_income', (
            (u'entry_ptr', self.gf('django.db.models.fields.related.OneToOneField')(to=orm['api.Entry'], unique=True, primary_key=True)),
            ('income_type', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['api.IncomeType'])),
        ))
        db.send_create_signal(u'api', ['Income'])

        # Adding model 'Budget'
        db.create_table(u'api_budget', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['api.BudgetUser'])),
            ('expense_type', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['api.ExpenseType'])),
            ('amount', self.gf('django.db.models.fields.FloatField')()),
        ))
        db.send_create_signal(u'api', ['Budget'])

        # Adding model 'Expense'
        db.create_table(u'api_expense', (
            (u'entry_ptr', self.gf('django.db.models.fields.related.OneToOneField')(to=orm['api.Entry'], unique=True, primary_key=True)),
            ('expense_type', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['api.ExpenseType'])),
        ))
        db.send_create_signal(u'api', ['Expense'])

        # Adding model 'IncomeType'
        db.create_table(u'api_incometype', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=255)),
        ))
        db.send_create_signal(u'api', ['IncomeType'])

        # Adding model 'Entry'
        db.create_table(u'api_entry', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['api.BudgetUser'])),
            ('amount', self.gf('django.db.models.fields.FloatField')()),
            ('date', self.gf('django.db.models.fields.DateField')()),
            ('description', self.gf('django.db.models.fields.TextField')(null=True, blank=True)),
            ('interval', self.gf('django.db.models.fields.CharField')(max_length=255)),
        ))
        db.send_create_signal(u'api', ['Entry'])

        # Adding model 'ExpenseType'
        db.create_table(u'api_expensetype', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=255)),
        ))
        db.send_create_signal(u'api', ['ExpenseType'])


    def backwards(self, orm):
        # Deleting model 'Income'
        db.delete_table(u'api_income')

        # Deleting model 'Budget'
        db.delete_table(u'api_budget')

        # Deleting model 'Expense'
        db.delete_table(u'api_expense')

        # Deleting model 'IncomeType'
        db.delete_table(u'api_incometype')

        # Deleting model 'Entry'
        db.delete_table(u'api_entry')

        # Deleting model 'ExpenseType'
        db.delete_table(u'api_expensetype')


    models = {
        u'api.budget': {
            'Meta': {'object_name': 'Budget'},
            'amount': ('django.db.models.fields.FloatField', [], {}),
            'expense_type': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['api.ExpenseType']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['api.BudgetUser']"})
        },
        u'api.budgetuser': {
            'Meta': {'object_name': 'BudgetUser', '_ormbases': [u'auth.User']},
            'currency': ('django.db.models.fields.CharField', [], {'max_length': '3'}),
            'total_budget': ('django.db.models.fields.FloatField', [], {'null': 'True', 'blank': 'True'}),
            u'user_ptr': ('django.db.models.fields.related.OneToOneField', [], {'to': u"orm['auth.User']", 'unique': 'True', 'primary_key': 'True'})
        },
        u'api.entry': {
            'Meta': {'object_name': 'Entry'},
            'amount': ('django.db.models.fields.FloatField', [], {}),
            'date': ('django.db.models.fields.DateField', [], {}),
            'description': ('django.db.models.fields.TextField', [], {'null': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'interval': ('django.db.models.fields.CharField', [], {'max_length': '255'}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['api.BudgetUser']"})
        },
        u'api.expense': {
            'Meta': {'object_name': 'Expense', '_ormbases': [u'api.Entry']},
            u'entry_ptr': ('django.db.models.fields.related.OneToOneField', [], {'to': u"orm['api.Entry']", 'unique': 'True', 'primary_key': 'True'}),
            'expense_type': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['api.ExpenseType']"})
        },
        u'api.expensetype': {
            'Meta': {'object_name': 'ExpenseType'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '255'})
        },
        u'api.income': {
            'Meta': {'object_name': 'Income', '_ormbases': [u'api.Entry']},
            u'entry_ptr': ('django.db.models.fields.related.OneToOneField', [], {'to': u"orm['api.Entry']", 'unique': 'True', 'primary_key': 'True'}),
            'income_type': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['api.IncomeType']"})
        },
        u'api.incometype': {
            'Meta': {'object_name': 'IncomeType'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '255'})
        },
        u'auth.group': {
            'Meta': {'object_name': 'Group'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '80'}),
            'permissions': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['auth.Permission']", 'symmetrical': 'False', 'blank': 'True'})
        },
        u'auth.permission': {
            'Meta': {'ordering': "(u'content_type__app_label', u'content_type__model', u'codename')", 'unique_together': "((u'content_type', u'codename'),)", 'object_name': 'Permission'},
            'codename': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'content_type': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['contenttypes.ContentType']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        },
        u'auth.user': {
            'Meta': {'object_name': 'User'},
            'date_joined': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'email': ('django.db.models.fields.EmailField', [], {'max_length': '75', 'blank': 'True'}),
            'first_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'groups': ('django.db.models.fields.related.ManyToManyField', [], {'symmetrical': 'False', 'related_name': "u'user_set'", 'blank': 'True', 'to': u"orm['auth.Group']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'is_active': ('django.db.models.fields.BooleanField', [], {'default': 'True'}),
            'is_staff': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'is_superuser': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'last_login': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'last_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'password': ('django.db.models.fields.CharField', [], {'max_length': '128'}),
            'user_permissions': ('django.db.models.fields.related.ManyToManyField', [], {'symmetrical': 'False', 'related_name': "u'user_set'", 'blank': 'True', 'to': u"orm['auth.Permission']"}),
            'username': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '30'})
        },
        u'contenttypes.contenttype': {
            'Meta': {'ordering': "('name',)", 'unique_together': "(('app_label', 'model'),)", 'object_name': 'ContentType', 'db_table': "'django_content_type'"},
            'app_label': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'model': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'})
        }
    }

    complete_apps = ['api']