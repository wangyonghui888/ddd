local orderNo = KEYS[1];
local userMatchPlayMarketKey = KEYS[2];
local userMatchPlayKey = KEYS[3]
local userMatchKey = KEYS[4]
local userKey = KEYS[5]
local singleMatchKey = KEYS[6]

local dateExpect = ARGV[2];
local busId = ARGV[3];
local matchId = ARGV[4];
local matchType = ARGV[5];
local playId = ARGV[6];
local playType = ARGV[7];
local betAmount = ARGV[8];
local odds = ARGV[9];
local isRecType = tonumber(ARGV[10]);
local userId = ARGV[11];
local optionId = ARGV[12];
local marketId = ARGV[13];
local currentPaidMoney = tonumber(ARGV[14]);
local sportId = ARGV[15];
local recData = loadstring("return " .. ARGV[16])();
local recLength = tonumber(ARGV[17]);

local userMatchPlayPaidMoneyByDB = tonumber(ARGV[18]);
local userMatchAllPaidAmountByDB = tonumber(ARGV[19]);
local userDayAllPaidAmountByDB = tonumber(ARGV[20]);
local singleMatchAllPaidAmountByDB = tonumber(ARGV[21]);

local result = redis.call('GET', orderNo);

local isSuccess = -1;
local errMsg = nil;
if result then
 	errMsg = "订单已存在，不重复收单";
	return {-1,errMsg};
end;

redis.call('SETEX', orderNo , 10 , '1');

local diffVal = 0;

local optionMaxPaidMoney = redis.call("HINCRBYFLOAT",userMatchPlayMarketKey,optionId,currentPaidMoney);
local allOrderBetMoney = redis.call("HINCRBYFLOAT",userMatchPlayMarketKey,'allOrderMoney',betAmount);
local redisMaxOptionPaid = redis.call("hget",userMatchPlayMarketKey,'maxOptionPaid');

if not redisMaxOptionPaid then
	redisMaxOptionPaid = 0;
end;

if tonumber(redisMaxOptionPaid) < tonumber(optionMaxPaidMoney) then
	redisMaxOptionPaid = optionMaxPaidMoney;
	redis.call("hset",userMatchPlayMarketKey,'maxOptionPaid',redisMaxOptionPaid);
end;

local maxPlayPaid = tonumber(redisMaxOptionPaid) - tonumber(allOrderBetMoney);
local redisMaxPlayPaid = redis.call("hget",userMatchPlayMarketKey,'MAX_PLAY_PAID');
redis.call("hset",userMatchPlayMarketKey,'MAX_PLAY_PAID' , maxPlayPaid);

if not redisMaxPlayPaid then
	redisMaxPlayPaid = 0;
end;

diffVal = maxPlayPaid - tonumber(redisMaxPlayPaid);

local secondKey = playId .. '_' .. matchType .. '_' .. playType;
local userMatchPlayPaidMoney = redis.call("HINCRBYFLOAT",userMatchKey,secondKey,diffVal);

if tonumber(userMatchPlayPaidMoney) > userMatchPlayPaidMoneyByDB then
 	errMsg = "用户玩法赔付限额拒单，配置：" .. userMatchPlayPaidMoneyByDB .. ",计算后："  ..  userMatchPlayPaidMoney ;
	return {-2,errMsg};
end;

if isRecType == 0 then
	local length = recLength;

	local min = nil;
	for i = 1,length do
		for j = 1 , length do
			local key = string.format("%03d",(i -1)) .. string.format("%03d",(j -1));
			local result = redis.call("HINCRBY",userMatchKey,key,recData[key]);
			if min == nil then
				min = result;
			end;
			if min > result then
				min = result;
			end;
		end;
	end;
	local recMaxPaid = redis.call("hget",userMatchKey,"REC_MAX_PAID");
	if not recMaxPaid then
		recMaxPaid = 0;
	end;

	diffVal = tonumber(recMaxPaid) - tonumber(min);

	redis.call("hset",userMatchKey,"REC_MAX_PAID" ,min);
end;

local userMatchAllPaidAmount = redis.call("HINCRBYFLOAT",userMatchKey,"USER_MATCH_ALL_PAID",diffVal);
if tonumber(userMatchAllPaidAmount) > userMatchAllPaidAmountByDB then
 	errMsg = "用户赛事限额拒单，配置：" .. userMatchAllPaidAmountByDB .. ",计算后："  ..  tonumber(userMatchAllPaidAmount) ;
	return {-3,errMsg};
end;


local userDayAllPaidAmount = redis.call("INCRBYFLOAT",userKey,diffVal);
if tonumber(userDayAllPaidAmount) > userDayAllPaidAmountByDB then
 	errMsg = "用户单日限额拒单，配置：" .. userDayAllPaidAmountByDB .. ",计算后："   .. tonumber(userDayAllPaidAmount) ;
	return {-4,errMsg};
end;

local singleMatchAllPaidAmount = redis.call("INCRBYFLOAT",singleMatchKey,diffVal);
if tonumber(singleMatchAllPaidAmount) > singleMatchAllPaidAmountByDB then
 	errMsg = "单场赛事限额拒单，配置：" .. singleMatchAllPaidAmountByDB .. ",计算后："  .. tonumber(singleMatchAllPaidAmount) ;
	return {-5,errMsg};
end;

return {1,'SUCCESS'};