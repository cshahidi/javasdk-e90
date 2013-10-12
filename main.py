#!/usr/bin/env python
# list buckets
import boto
from datetime import datetime

s3 = boto.connect_s3()
rs = s3.get_all_buckets()
print '%s buckets found.' % len(rs)
for b in rs:
    print b.name


bucket_name = 'uniquebucketname' + datetime.now().isoformat().replace(':', '-').lower()
s3.create_bucket(bucket_name)
 
print 'Creating a bucket %s ' % bucket_name
bucklist = s3.get_all_buckets()  # GET Service
for b in bucklist:
    if b.name == bucket_name:
        print 'Found bucket we created. Creation date = %s' % b.creation_date
 
print 'Deleting the bucket.'
 
s3.delete_bucket(bucket_name)
