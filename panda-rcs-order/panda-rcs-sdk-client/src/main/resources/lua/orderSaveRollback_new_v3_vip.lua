local dateExpect = ARGV[1];
local busId = ARGV[2];
local sportId = ARGV[3];
local userId = ARGV[4];
local matchId = ARGV[5];
local playId = ARGV[6];
local marketId = ARGV[7];
local matchType = ARGV[8];
local playType = ARGV[9];
local orderId = ARGV[10];
local betAmount = tonumber(ARGV[11]);
local isRecType = tonumber(ARGV[12]);
local optionId = ARGV[13];
local currentPaidMoney = ARGV[14];

local recData = ARGV[15];
local recLength = tonumber(ARGV[16]);
local userMatchAllPaidAmountByDB = tonumber(ARGV[17]);
local odd = tonumber(ARGV[18]);

local resultTable = {};
resultTable["1"] = betAmount;
resultTable["2"] = betAmount / 2;
resultTable["3"] = betAmount * (odd - 1) * -1;
resultTable["4"] = betAmount / 2 * (odd - 1) * -1 ;
resultTable["5"] = 0;

local mytable = {};
mytable["0"] = {"0","0"};mytable["1"] = {"0","1"};mytable["2"] = {"0","2"};mytable["3"] = {"0","3"};mytable["4"] = {"0","4"};mytable["5"] = {"0","5"};mytable["6"] = {"0","6"};mytable["7"] = {"0","7"};mytable["8"] = {"0","8"};mytable["9"] = {"0","9"};mytable["A"] = {"1","0"};mytable["B"] = {"1","1"};mytable["C"] = {"1","2"};mytable["D"] = {"1","3"};mytable["E"] = {"1","4"};mytable["F"] = {"1","5"};mytable["G"] = {"1","6"};mytable["H"] = {"1","7"};mytable["I"] = {"1","8"};mytable["J"] = {"1","9"};mytable["K"] = {"2","0"};mytable["L"] = {"2","1"};mytable["M"] = {"2","2"};mytable["N"] = {"2","3"};mytable["O"] = {"2","4"};mytable["P"] = {"2","5"};mytable["Q"] = {"2","6"};mytable["R"] = {"2","7"};mytable["S"] = {"2","8"};mytable["T"] = {"2","9"};mytable["U"] = {"3","0"};mytable["V"] = {"3","1"};mytable["W"] = {"3","2"};mytable["X"] = {"3","3"};mytable["Y"] = {"3","4"};mytable["Z"] = {"3","5"};mytable["a"] = {"3","6"};mytable["b"] = {"3","7"};mytable["c"] = {"3","8"};mytable["d"] = {"3","9"};mytable["e"] = {"4","0"};mytable["f"] = {"4","1"};mytable["g"] = {"4","2"};mytable["h"] = {"4","3"};mytable["i"] = {"4","4"};mytable["j"] = {"4","5"};mytable["k"] = {"4","6"};mytable["l"] = {"4","7"};mytable["m"] = {"4","8"};mytable["n"] = {"4","9"};mytable["o"] = {"5","0"};mytable["p"] = {"5","1"};mytable["q"] = {"5","2"};mytable["r"] = {"5","3"};mytable["s"] = {"5","4"};mytable["t"] = {"5","5"};mytable["u"] = {"5","6"};mytable["v"] = {"5","7"};mytable["w"] = {"5","8"};mytable["x"] = {"5","9"};mytable["y"] = {"6","0"};mytable["z"] = {"6","1"};

local suffix = "_{" .. busId .. "_" .. matchId .. "}";
local prefix = "RCS:RISK:SPECIAL:" .. dateExpect .. ":" .. busId .. ":" .. sportId .. ":";
local userMatchPlayMarketKey = prefix .. userId .. ":" .. matchId .. ":" .. playId .. ":" .. marketId .. ":" .. matchType .. ":" .. playType .. suffix;
local userMatchKey = prefix .. userId .. ":" .. matchId ..  suffix;
local userKey = prefix .. userId .. ":" .. suffix;
local singleMatchInfoKey = prefix .. matchId .. ":" .. matchType .. ":V2" .. suffix;
local singleMatchMarketKey = prefix .. matchId .. ":" .. playId .. ":" .. marketId .. suffix;

local code = tonumber(ARGV[19]);

if code == 1 then 
	code = -100;
end;

if code >= -1 then 
	return {-1,'订单已存在异常不做处理'};
end;


local isSuccess = -1;
local errMsg = nil;
local diffVal = 0;

local optionMaxPaidMoney = redis.call("HINCRBYFLOAT",userMatchPlayMarketKey,optionId,currentPaidMoney * -1);
local allOrderBetMoney = redis.call("HINCRBYFLOAT",userMatchPlayMarketKey,'allOrderMoney',betAmount * -1);
local redisMaxOptionPaid = redis.call("hget",userMatchPlayMarketKey,'maxOptionPaid');

local userMatchPlayMarketKeyList = redis.call('HGETALL', userMatchPlayMarketKey);
local currentOptionMaxPaidMoney = 0;
for i = 1,#userMatchPlayMarketKeyList,2 do
	local tempKey = userMatchPlayMarketKeyList[i];
	if not (tempKey == 'allOrderMoney' or tempKey == 'maxOptionPaid' or tempKey == 'MAX_PLAY_PAID') then 
		if currentOptionMaxPaidMoney < tonumber(userMatchPlayMarketKeyList[i + 1]) then 
			currentOptionMaxPaidMoney = tonumber(userMatchPlayMarketKeyList[i + 1]);
		end;
	end;
end;

redisMaxOptionPaid = currentOptionMaxPaidMoney;
redis.call("hset",userMatchPlayMarketKey,'maxOptionPaid',redisMaxOptionPaid);

local maxPlayPaid = tonumber(redisMaxOptionPaid) - tonumber(allOrderBetMoney);
local redisMaxPlayPaid = redis.call("hget",userMatchPlayMarketKey,'MAX_PLAY_PAID');
redis.call("hset",userMatchPlayMarketKey,'MAX_PLAY_PAID' , maxPlayPaid);

diffVal = maxPlayPaid - redisMaxPlayPaid;

local secondKey = playId .. '_' .. matchType .. '_' .. playType;
redis.call("HINCRBYFLOAT",userMatchKey,secondKey,diffVal);

if isRecType == 0 then
	local min = nil;

    local func_itor = {};
    local allKeysResult = redis.call("HGET",userMatchKey,"ALL_KEYS");
    if allKeysResult then
    	func_itor = loadstring("return {" .. allKeysResult .. "}")();
    end;

    local index = 1;
    for i in string.gmatch(recData,"(.-),") do
    	local tempI = mytable[i];
    	if tempI == nil then
    	    break;
   		end;
   		for j = 1 , #tempI do
   			local currentResult = tempI[j];
   			if currentResult == "9" then
   				break;
   			end;
    		local money = resultTable[currentResult];
            func_itor[index] = func_itor[index] - money;

    		if min == nil or min > func_itor[index] then
    			min = func_itor[index];
    		end;

    		index = index + 1;
    	end;
    end;
	redis.call("hset",userMatchKey,"ALL_KEYS",table.concat(func_itor,","));
	local recMaxPaid = redis.call("hget",userMatchKey,"REC_MAX_PAID");
	if not recMaxPaid then
		recMaxPaid = 0;
	end;

	diffVal = recMaxPaid - min;

	redis.call("hset",userMatchKey,"REC_MAX_PAID" ,min);
end;

redis.call("HINCRBYFLOAT",userMatchKey,"USER_MATCH_ALL_PAID",diffVal);

return {1,'回滚成功'};