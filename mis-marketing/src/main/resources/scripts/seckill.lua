local stockKey = KEYS[1]
local userHistoryKey = KEYS[2]
local userId = ARGV[1]

-- 1. 校验是否重复领取
if redis.call('sismember', userHistoryKey, userId) == 1 then
    return -1
end

-- 2. 校验库存
local stock = tonumber(redis.call('get', stockKey) or "0")
if stock <= 0 then
    return -2
end

-- 3. 扣库存并记录
redis.call('decr', stockKey)
redis.call('sadd', userHistoryKey, userId)

return 0