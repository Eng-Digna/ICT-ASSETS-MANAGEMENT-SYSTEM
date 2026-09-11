export const assets = [
  { id: 'SN-TPA-00231', type: 'Laptop', brand: 'Dell Latitude 5420', assignedUser: 'F. Ngoma', department: 'Finance', station: 'Dar es Salaam', status: 'Assigned', serialNumber: 'SN-TPA-00231', purchaseDate: '2023-04-18', notes: 'Primary finance reporting laptop.' },
  { id: 'SN-TPA-00119', type: 'Printer', brand: 'HP LaserJet M404', assignedUser: 'Unassigned', department: 'ICT', station: 'Tanga', status: 'Pending Disposal', serialNumber: 'SN-TPA-00119', purchaseDate: '2019-08-12', notes: 'Awaiting disposal committee review.' },
  { id: 'SN-TPA-00098', type: 'Scanner', brand: 'Canon LiDE 400', assignedUser: 'Unassigned', department: 'ICT', station: 'Mtwara', status: 'Disposed', serialNumber: 'SN-TPA-00098', purchaseDate: '2018-02-20', notes: 'Disposed after recurring hardware faults.' },
  { id: 'SN-TPA-00204', type: 'Desktop', brand: 'HP ProDesk 400', assignedUser: 'A. Komba', department: 'Operations', station: 'Kigoma', status: 'Assigned', serialNumber: 'SN-TPA-00204', purchaseDate: '2022-11-02', notes: '' },
  { id: 'SN-TPA-00256', type: 'Laptop', brand: 'Lenovo ThinkPad E14', assignedUser: 'Unassigned', department: 'ICT', station: 'Mwanza', status: 'Under Maintenance', serialNumber: 'SN-TPA-00256', purchaseDate: '2023-06-15', notes: 'Keyboard replacement in progress.' },
  { id: 'SN-TPA-00287', type: 'Desktop', brand: 'Dell OptiPlex 3090', assignedUser: 'N. Mfinanga', department: 'Human Resources', station: 'Dar es Salaam', status: 'Assigned', serialNumber: 'SN-TPA-00287', purchaseDate: '2023-09-07', notes: '' },
  { id: 'SN-TPA-00301', type: 'Monitor', brand: 'Dell P2422H', assignedUser: 'J. Mushi', department: 'ICT', station: 'Dar es Salaam', status: 'Available', serialNumber: 'SN-TPA-00301', purchaseDate: '2024-01-22', notes: '' },
  { id: 'SN-TPA-00312', type: 'Laptop', brand: 'HP ProBook 450 G9', assignedUser: 'J. Mushi', department: 'Administration', station: 'Dar es Salaam', status: 'Assigned', serialNumber: 'SN-TPA-00312', purchaseDate: '2024-02-14', notes: '' },
  { id: 'SN-TPA-00319', type: 'Router', brand: 'Cisco ISR 4331', assignedUser: 'Unassigned', department: 'ICT', station: 'Zanzibar', status: 'Available', serialNumber: 'SN-TPA-00319', purchaseDate: '2022-07-30', notes: 'Spare network equipment.' },
  { id: 'SN-TPA-00328', type: 'Desktop', brand: 'Lenovo ThinkCentre M70s', assignedUser: 'P. John', department: 'Procurement', station: 'Tanga', status: 'Assigned', serialNumber: 'SN-TPA-00328', purchaseDate: '2023-10-11', notes: '' },
  { id: 'SN-TPA-00335', type: 'Projector', brand: 'Epson EB-X06', assignedUser: 'Unassigned', department: 'Corporate Affairs', station: 'Dar es Salaam', status: 'Under Maintenance', serialNumber: 'SN-TPA-00335', purchaseDate: '2021-05-09', notes: 'Lamp replacement scheduled.' },
  { id: 'SN-TPA-00341', type: 'Printer', brand: 'Brother HL-L5100DN', assignedUser: 'Unassigned', department: 'Finance', station: 'Mtwara', status: 'Available', serialNumber: 'SN-TPA-00341', purchaseDate: '2024-03-18', notes: '' }
];

export const assetTypes = [...new Set(assets.map((asset) => asset.type))].sort();
export const departments = [...new Set(assets.map((asset) => asset.department))].sort();
export const stations = [...new Set(assets.map((asset) => asset.station))].sort();