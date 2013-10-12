bucket = AWS::S3.new.buckets['mybucket']

#bucket.objects.delete('key1', 'key2', 'key3', ...)

# delete all of the objects in a bucket (optionally with a common prefix as shown)
bucket.objects.with_prefix('2009/').delete_all

# empty the bucket and then delete the bucket, objects are deleted in batches of 1k
bucket.delete!

AWS::S3::Bucket.delete('your_bucket', :force => true)