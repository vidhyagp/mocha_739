from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'mocha.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    #url(r'^admin/', include(admin.site.urls)),
    url(r'^login/', 'mocha_models.views.loginview'),
    url(r'^auth/', 'mocha_models.views.auth_and_login'),
    url(r'^signup/', 'mocha_models.views.sign_up_in'),
    url(r'^$', 'mocha_models.views.render_topics'),
    url(r'^tabs', 'mocha_models.views.render_topics'),
    url(r'^home', 'mocha_models.views.render_topics'),
    url(r'^createTopic', 'mocha_models.views.create_topic'),
    url(r'^subscribeTopic', 'mocha_models.views.subscribe_topic'),
    url(r'^topic/(?P<topic>.*)', 'mocha_models.views.render_content'),
)
