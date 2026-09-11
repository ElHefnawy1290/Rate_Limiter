local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local weight = tonumber(ARGV[4])

local redis_data = redis.call("HMGET", key, "tokens", "last_time")
local tokens = tonumber(redis_data[1])
local last_time = tonumber(redis_data[2])

if tokens == nil then
    tokens = capacity
    last_time = now
else
    tokens = math.min(tokens + rate * (math.max(now - last_time, 0)), capacity)
    last_time = now
end

if tokens>=weight then
    tokens = tokens - weight
    redis.call("HMSET", key, "tokens", tokens, "last_time", last_time)
    redis.call("EXPIRE", key, 120)
    return 1
else
    redis.call("EXPIRE", key, 120)
    return 0
end
