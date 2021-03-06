import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {Scrollbars} from 'react-custom-scrollbars';
import {connect} from 'react-redux';
import ActionsUtils from '../../actions/ActionsUtils';
import ProcessUtils from '../../common/ProcessUtils';
import {v4 as uuid4} from "uuid";
import Errors from "./Errors"
import Warnings from "./Warnings"
import ValidTip from "./ValidTip"

export class Tips extends Component {

  static propTypes = {
    currentProcess: PropTypes.object,
    grouping: PropTypes.bool.isRequired,
    isHighlighted: PropTypes.bool,
    testing: PropTypes.bool.isRequired
  }

  showDetails = (event, node) => {
    event.preventDefault()
    this.props.actions.displayModalNodeDetails(node)
  }

  render() {
    const {currentProcess, grouping, testing} = this.props
    const errors = (currentProcess.validationResult || {}).errors
    const warnings = (currentProcess.validationResult || {}).warnings

    return (
      <div id="tipsPanel" className={this.props.isHighlighted ? "tipsPanelHighlighted" : "tipsPanel"}>
        <Scrollbars renderThumbVertical={props => <div key={uuid4()} {...props} className="thumbVertical"/>}
                    hideTracksWhenNotNeeded={true}>
          {ProcessUtils.hasNeitherErrorsNorWarnings(currentProcess) && <ValidTip grouping={grouping} testing={testing}/>}
          {!ProcessUtils.hasNoErrors(currentProcess) && <Errors errors={errors}
                                                                showDetails={this.showDetails}
                                                                currentProcess={currentProcess}/>}
          {!ProcessUtils.hasNoWarnings(currentProcess) && <Warnings warnings={ProcessUtils.extractInvalidNodes(warnings.invalidNodes)}
                                                                    showDetails={this.showDetails}
                                                                    currentProcess={currentProcess}/>}
        </Scrollbars>
      </div>
    );
  }
}

function mapState(state) {
  return {
    currentProcess: state.graphReducer.processToDisplay || {},
    grouping: state.graphReducer.groupingState != null,
    isHighlighted: state.ui.isToolTipsHighlighted,
    testing: !!state.graphReducer.testResults
  };
}

export default connect(mapState, ActionsUtils.mapDispatchWithEspActions)(Tips);
