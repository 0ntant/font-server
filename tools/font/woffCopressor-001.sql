UPDATE font
SET file_path = REPLACE(file_path, '.ttf', '.woff2')
WHERE file_path LIKE '%.ttf';