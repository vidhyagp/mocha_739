mocha_739
=========

739 project
sivas


mysql-python
dpkg-reconfigure mysql-server-5.5

 inst python-imaging python-pythonmagick python-markdown python-textile python-docutils
  896  pip install Django==1.6
  898  pip install Django==1.6
  899  sudo pip install Django==1.6
  901  inst mysql
  902  inst php-mysql
  903  inst mysql-server
  939  mysql_install_db
  940  sudo mysql_install_db
  945  inst mysql-server
  959  inst xclip
  967  pip install MySQL-python
  968  sudo pip install MySQL-python
  969  inst python-mysqldb
  970  sudo pip install MySQL-python
  975  inst git-core



To_do on remote :
install django tabs using following command :
svn checkout http://django-tabs.googlecode.com/svn/trunk/ django-tabs-read-only
in tabs.py change _ACTIVETABS to ACTIVETABS
in settings.py add 'tabs' to INSTALLED_APPS
run 'sudo python setup.py install'


foreman start --use-python=/var/www/virtualenv/bin/python
git remote add heroku [address]



Install Celery
--------------
refer http://www.celeryproject.org/instal/
pip install Celery
pip install django-celery
pip install djkombu

Add to INSTALLED_APPS
'djcelery'
'kombu.transport.django'
'djkombu'

Add the 3 lines below
import djcelery
djcelery.setup_loader()
BROKER_URL="django://"


Execute the following two commands

python manage.py syncdb
python manage.py celeryd -l info

