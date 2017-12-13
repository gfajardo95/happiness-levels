import json


def convertToJSON(dict):
    json_repr = json.dumps(dict)
    json_repr = json.loads(json_repr)

    return json_repr