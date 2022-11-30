import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Redirect, withRouter} from 'react-router-dom';
import {translate} from 'react-i18next';
import {Link} from 'react-router-dom';
import {
  login
} from '../../services/application';
import Button from '../buttons/button';
import LoginSnackbar from './login-snackbar';
import TextField from '../form/text-field/text-field';
import Spinner from '../spinner/spinner';
import mainCss from '../../styles/main.scss';
import styles from './login.scss';

/**
 * Returns a Login component valid state from the current URL params
 *
 * @param {URLSearchParams} params
 * @returns {{values: {user: string, password: string}}}
 */
const stateFromParams = params => ({
  values: {
    user: params.has('user') ? params.get('user') : '',
    password: ''
  }
});

const stateFromFormValues = formValues => ({
  values: {...formValues, password: ''}
});

export class Login extends Component {
  constructor(props) {
    super(props);
    this.state = stateFromParams(new URLSearchParams(this.props.location.search));
    if (this.props.formValues && Object.keys(this.props.formValues).length > 0) {
      this.state = stateFromFormValues(this.props.formValues);
    }
    this.onFieldChange = this.onFieldChange.bind(this);
    this.login = this.login.bind(this);
  }

  render() {
    const t = this.props.t;
    const {user, password} = this.state.values;
    if (this.props.application.user.credentials) {
      return <Redirect to="/"/>;
    }
    return (
      <div className={styles['login--background']}>
        <div className={styles['login--container']}>
          <Spinner
            visible={this.props.application.activeRequests > 0}
            className={styles.spinner} pathClassName={styles.spinnerPath}/>
          <div className={`${mainCss['mdc-card']} ${styles.card}`}>
            <header>
              <h1 className={styles.title}>{this.props.application.title}</h1>
              <h2 className={styles.subtitle}>{t('login.Login')}</h2>
            </header>
            <form onSubmit={this.login}>
              <TextField id='user' fieldClass={`${styles.formField} ${styles.fullWidth}`}
                value={user} onChange={this.onFieldChange}
                focused={this.isFocused('user')} required={true} autoComplete='on' label={t('login.User')}/>
              <TextField id='password' type={'password'} fieldClass={`${styles.formField} ${styles.fullWidth}`}
                value={password} onChange={this.onFieldChange}
                focused={this.isFocused('password')} required={true} label={t('login.Password')}
              />
              <Button type={'submit'}
                className={`${styles.loginButton} ${mainCss['mdc-button--unelevated']} ${styles.fullWidth}`}
                label={t('login.actions.Login')} />
              <Link to="/adminpanel">{t('login.adminPanel')}</Link>
            </form>
          </div>
          <LoginSnackbar />
        </div>
      </div>
    );
  }

  isFocused(componentId) {
    return componentId === this.state.focusedComponentId;
  }

  onToggle(id) {
    this.setState(prevState => {
      const newState = {...prevState};
      newState.values = {...prevState.values};
      newState.values[id] = !newState.values[id];
      return newState;
    });
  }

  onFieldChange(event) {
    const target = event.target;
    this.setState(prevState => {
      const newState = {...prevState};
      newState.focusedComponentId = target.id;
      newState.values = {...prevState.values};
      newState.values[target.id] = target.value;
      return newState;
    });
  }


  login(event) {
    event.preventDefault();
    this.props.dispatchLogin(this.state.values);
  }
}

Login.propTypes = {
};

const mapStateToProps = state => ({
  application: state.application,
  formValues: state.login.formValues
});

const mapDispatchToProps = dispatch => ({
  dispatchLogin: credentials => login(dispatch, credentials)
});

export default connect(mapStateToProps, mapDispatchToProps)(translate()(withRouter(Login)));
