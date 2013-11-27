from django import forms
from mocha_models.models import *

class TopicContentForm(forms.ModelForm):
    class Meta:
        model = TopicContent
        fields = ['topic', 'content', 'userid']
	exclude = ['userid']

class UserTopicForm(forms.ModelForm):
    class Meta:
        model = UserTopic
        fields = ['userid', 'topic']
	exclude = ['userid']


