import React from 'react';
import {shallow} from 'enzyme/build/index';
import {Login} from '../login';
import {INITIAL_STATE} from '../../../reducers';

describe('LoginSnackbar component test suite', () => {
  describe('Snapshot render', () => {
    test('User not logged in, should render Login', () => {
      // Given
      const props = {
        t: jest.fn(messageKey => messageKey),
        location: {
          search: ''
        },
        application: INITIAL_STATE.application
      };

      // When
      const login = shallow(<Login {...props}/>);

      // Then
      expect(login).toMatchSnapshot();
    });
    test('User not logged in with advanced toggled on, should render Login with advanced view', () => {
      // Given
      const props = {
        t: jest.fn(messageKey => messageKey),
        location: {
          search: ''
        },
        application: INITIAL_STATE.application
      };

      // When
      const login = shallow(<Login {...props}/>);
      login.setState({advanced: true});

      // Then
      expect(login).toMatchSnapshot();
    });
    test('User logged in, should render Redirect', () => {
      // Given
      const props = {
        t: jest.fn(messageKey => messageKey),
        location: {
          search: ''
        },
        application: {...INITIAL_STATE.application, user: {credentials: {encrypted: 'encrypted', salt: 'salt'}}}
      };

      // When
      const login = shallow(<Login {...props}/>);

      // Then
      expect(login).toMatchSnapshot();
    });
  });
  describe('Initial values', () => {
    test('Initial values from URL Params', () => {
      // Given
      const location = {
        search: '?user=user@user'
      };
      const props = {
        t: jest.fn(messageKey => messageKey),
        location,
        application: INITIAL_STATE.application
      };
      // When
      const login = shallow(<Login {...props}/>);
      // Then
      expect(login.state().values.user).toBe('user@user');
    });
    test('Initial values from Redux', () => {
      // Given
      const location = {
        search: '?serverHost=server.host'
      };
      const props = {
        t: jest.fn(messageKey => messageKey),
        location,
        formValues: {
          user: 'user@redux',
        },
        application: INITIAL_STATE.application
      };
      // When
      const login = shallow(<Login {...props}/>);
      // Then
      expect(login.state().values.user).toBe('user@redux');
    });
  });
});
