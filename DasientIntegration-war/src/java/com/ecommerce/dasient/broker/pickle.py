import java.util
import pickle
import simplejson as json
from decimal import Decimal

#
# Pickles a CleanWorkRequest into a format understood by the cleaning agents
#

def pickle_work(output, delay, revision, rules, orders):
    pickle.dump({
        'delay': delay,
        'ruleSet': {
            'revision': revision and revision.id,
            'rules': rules_to_py(rules)
        },
        'workUnits': work_orders_to_py(orders)
    }, output)

def rules_to_py(rules):
    if rules is not None:
        return [rule_to_py(rule) for rule in rules]
    else:
        return None

def rule_to_py(rule):
    return {
        'id': rule.state.id,
        'action' : rule.state.action.id,
        'terms': rule_terms_to_py(rule.state.terms)
    }

def rule_terms_to_py(terms):
    if terms is not None:
        return tuple([entry.term for entry in terms])
    else:
        return ()

def work_orders_to_py(orders):
    if orders is not None:
        return [work_order_to_py(order) for order in orders]
    else:
        return None

def work_order_to_py(order):
    return {
        'workUnitId': order.workUnitId,
        'controlPanel': order.controlPanel,
        'webServer': order.webServer,
        'webUsername': order.webUsername,
        'accountId': order.accountId,
        'triggers': scan_history_to_py(order.triggers)
    }

def scan_history_to_py(history):
    if history is not None:
        return [scan_history_entry_to_py(entry) for entry in history]
    else:
        return None

def scan_history_entry_to_py(entry):
    return {
        'id': entry.scanHistoryId,
        'rawRequest': entry.rawRequest and json.loads(entry.rawRequest),
        'rawResponse': entry.rawResponse and json.loads(entry.rawResponse)
    }

#
# Unpickles a response from a cleaning agent into a CleanWorkResponse
#

from com.ecommerce.dasient.vo import CleanWorkResponse

def timestamp_to_java_date(timestamp):
    fixed_point_timestamp = Decimal(str(timestamp))
    milliseconds_since_epoch = long(fixed_point_timestamp * 1000L)
    return java.util.Date(milliseconds_since_epoch)

def unpickle_work_response(input):
    pyresponse = pickle.load(input)

    response = CleanWorkResponse()
    response.workUnitId = long(pyresponse['workUnitId'])
    response.ruleSetId = long(pyresponse['ruleSetId'])
    response.startTime = timestamp_to_java_date(pyresponse['start'])
    response.finishTime = timestamp_to_java_date(pyresponse['finish'])
    response.files = java.util.ArrayList()

    for path, pyfile in pyresponse['files'].items():
        file = CleanWorkResponse.File()
        file.path = str(path)
        file.modifyTime = timestamp_to_java_date(pyfile[1])
        file.changeTime = timestamp_to_java_date(pyfile[2])
        file.newChangeTime = timestamp_to_java_date(pyfile[3])
        file.mode = int(pyfile[4])
        file.hash = str(pyfile[5])
        file.matches = java.util.ArrayList()

        for pymatch in pyfile[0]:

            match = CleanWorkResponse.File.Match()
            match.ruleId = long(pymatch[0])
            match.start = int(pymatch[1])
            match.end = int(pymatch[2])
            match.literal = str(pymatch[3])

            file.matches.add(match)

        response.files.add(file)

    return response
