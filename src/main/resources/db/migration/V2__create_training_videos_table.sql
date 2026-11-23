CREATE TABLE training_videos (
   id UUID PRIMARY KEY,
   title VARCHAR(200) NOT NULL,
   description VARCHAR(1000),
   object_key VARCHAR(255) NOT NULL UNIQUE,
   content_type VARCHAR(100) NOT NULL,
   file_size BIGINT NOT NULL,
   duration_seconds BIGINT,
   status VARCHAR(20) NOT NULL,
   created_at TIMESTAMP WITH TIME ZONE NOT NULL,
   updated_at TIMESTAMP WITH TIME ZONE,
   CONSTRAINT chk_training_video_status
       CHECK (status IN ('PENDING', 'ACTIVE', 'INACTIVE')),
   CONSTRAINT chk_training_video_file_size
       CHECK (file_size > 0),
   CONSTRAINT chk_training_video_duration
       CHECK (duration_seconds IS NULL OR duration_seconds >= 0)
);