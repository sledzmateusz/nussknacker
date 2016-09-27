import NodeUtils from './NodeUtils'

export default {

  mapProcessWithNewNode: (process, before, after) => {
    return {
      ...process,
      edges: _.map(process.edges, (e) => {
        if (_.isEqual(e.from, before.id)) {
          return {from: after.id, to: e.to}
        } else if (_.isEqual(e.to, before.id)) {
          return {from: e.from, to: after.id}
        } else {
          return e
        }
      }),
      nodes: _.map(process.nodes, (n) => { return _.isEqual(n, before) ? after : n }),
      properties: NodeUtils.nodeIsProperties(before) ? after : process.properties
    }
  }

}