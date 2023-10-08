local current = redis.call('incrBy', KEYS[1], ARGV[1]);
if current == tonumber(ARGV[1]) then
    local t = redis.call('ttl', KEYS[1]);
    if t == -1 then
        redis.call('expire', KEYS[1], ARGV[2])
    end;
end;
return current